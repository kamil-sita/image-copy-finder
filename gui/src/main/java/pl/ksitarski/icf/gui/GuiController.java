package pl.ksitarski.icf.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.accessing.LoadableImageFile;
import pl.ksitarski.icf.core.comparison.*;
import pl.ksitarski.icf.core.comparison.comparator.ComplexImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.ComparisonProgress;
import pl.ksitarski.icf.core.comparison.engine.IcfEngineDispatcher;
import pl.ksitarski.icf.core.comparison.engine.IcfSettings;
import pl.ksitarski.icf.core.db.IcfCollection;
import pl.ksitarski.icf.core.exc.AlgorithmFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuiController {

    @FXML
    private ListView<String> databasesList;

    @FXML
    private ListView<String> fileList;

    @FXML
    private Accordion leftPaneAccordion;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressBar progressBar1;

    @FXML
    private Label feedbackLabel;

    @FXML
    private ImageView centerImageView;

    @FXML
    private ListView<ComparisonListElement> comparisonList;

    @FXML
    private ChoiceBox<ComplexImageComparator> algorithmSelection;

    @FXML
    private AnchorPane centerAnchorPane;

    @FXML
    private AnchorPane leftImageAnchorPane;

    @FXML
    private AnchorPane rightImageAnchorPane;

    @FXML
    private Slider cutoffSlider;

    @FXML
    private ImageView leftImageView;

    @FXML
    private ImageView rightImageView;

    private String chosenDatabase = null;
    private String chosenFile = null;
    private ImageComparingResults imageComparingResults;

    public void initialize() {
        leftImageView.fitWidthProperty().bind(leftImageAnchorPane.widthProperty());
        leftImageView.fitHeightProperty().bind(leftImageAnchorPane.heightProperty());

        rightImageView.fitWidthProperty().bind(rightImageAnchorPane.widthProperty());
        rightImageView.fitHeightProperty().bind(rightImageAnchorPane.heightProperty());

        centerImageView.fitWidthProperty().bind(centerAnchorPane.widthProperty());
        centerImageView.fitHeightProperty().bind(centerAnchorPane.heightProperty());



        ObservableList<ComplexImageComparator> cics = FXCollections.observableArrayList();
        cics.add(Comparators.cic5);
        algorithmSelection.setItems(cics);

        algorithmSelection.getSelectionModel().select(0);

        cutoffSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateResults();
            }
        });


        databasesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                chosenDatabase = newValue;
                if (newValue != null) {
                    loadFilesDatabase();
                } else {
                    filesDatabaseClear();
                }
            }
        });

        fileList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                chosenFile = newValue;
                loadImage(newValue, centerImageView);
                leftImageView.setImage(null);
                rightImageView.setImage(null);
            }
        });

        comparisonList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ComparisonListElement>() {
            @Override
            public void changed(ObservableValue<? extends ComparisonListElement> observable, ComparisonListElement oldValue, ComparisonListElement newValue) {
                centerImageView.setImage(null);
                if (newValue == null) {
                    return;
                }
                loadImage(newValue.getLoadableImage0(), leftImageView);
                loadImage(newValue.getLoadableImage1(), rightImageView);
            }
        });


        refreshDatabases(false);

    }

    private void loadImage(String path, ImageView imageView) {
        if (path == null) {
            imageView.setImage(null);
        } else {
            File file = new File(path);
            if (!file.exists()) {
                updateFeedback("File does not exist!");
                return;
            }
            Image image = new Image(file.toURI().toString());
            if (image.isError()) {
                updateFeedback("Error while reading image");
                imageView.setImage(null);
                return;
            }
            imageView.setImage(image);
        }
    }

    private void loadImage(LoadableImage loadableImage, ImageView imageView) {
        if (loadableImage instanceof ImageInDatabase) {
            loadImage(((ImageInDatabase) loadableImage).getFullPath(), imageView);
        }
        if (loadableImage instanceof LoadableImageFile) {
            loadImage(((LoadableImageFile) loadableImage).getPath().toString(), imageView);
        }
    }

    private void filesDatabaseClear() {
        fileList.setItems(FXCollections.observableArrayList());
    }


    private void loadFilesDatabase() {
        loadFilesDatabase(true, true);
    }

    private void loadFilesDatabase(boolean manipulateGui, boolean text) {
        if (manipulateGui) {
            disableGui();
        }
        execute(new Job() {
            List<String> fileNames;

            @Override
            public void runNow() {
                fileNames = new ArrayList<>();
                List<ImageInDatabase> images = IcfCollection.getCollection(chosenDatabase, "").getFiles();
                for (ImageInDatabase imageInDatabase : images) {
                    fileNames.add(imageInDatabase.getFullPath());
                }
            }

            @Override
            public void runOnSuccess() {
                ObservableList<String> files = FXCollections.observableArrayList();
                files.addAll(fileNames);
                fileList.setItems(files);
                setProgressLoading(0);
                setProgressProcessing(0);
                if (manipulateGui) {
                    enableGui();
                }
                if (text) {
                    updateFeedback("Loaded files for database \"" + chosenDatabase + "\"");
                }
            }
        });
    }


    @FXML
    void deleteDatabaseButton(ActionEvent event) {
        if (chosenDatabase != null) {
            disableGui();
            IcfCollection.removeCollection(chosenDatabase);
            refreshDatabases(false, false);
            updateFeedback("Removed database \"" + chosenDatabase + "\"");
            chosenDatabase = null;
            enableGui();
        } else {
            updateFeedback("Must select database first");
        }
    }

    private void refreshDatabases(boolean updateFeedback) {
        refreshDatabases(updateFeedback, true);
    }

    private void refreshDatabases(boolean updateFeedback, boolean modifyGui) {
        if (modifyGui) {
            disableGui();
            setProgressUnsure();
        }
        execute(new Job() {
            List<String> dbNames;

            @Override
            public void runNow() {
                dbNames = IcfCollection.getCollectionsNames();
            }

            @Override
            public void runOnSuccess() {
                ObservableList<String> dbs = FXCollections.observableArrayList();
                dbs.addAll(dbNames);
                databasesList.setItems(dbs);
                if (modifyGui) {
                    setProgressLoading(0);
                    setProgressProcessing(0);
                    enableGui();
                }
                if (updateFeedback) {
                    updateFeedback("Reloaded databases");
                }
            }
        });
    }


    @FXML
    void addFile(ActionEvent event) {
        if (chosenDatabase == null) {
            updateFeedback("Can't choose file without selected database!");
            return;
        }
        Optional<File> file = getFile();
        if (file.isPresent()) {
            IcfCollection.getCollection(chosenDatabase, "")
                    .addOrFindFile(file.get().toPath());
            loadFilesDatabase();
            updateFeedback("Added file!");
        }
    }


    @FXML
    void addFiles(ActionEvent event) {
        if (chosenDatabase == null) {
            updateFeedback("Can't choose files without selected database!");
            return;
        }
        List<File> files = getFiles();
        disableGui();
        execute(new Job() {

            @Override
            public void runNow() {
                IcfCollection database = IcfCollection.getCollection(chosenDatabase, "");
                List<Path> paths = new ArrayList<>();
                for (File file : files) {
                    paths.add(file.toPath());
                }
                database.addOrFindFiles(paths);
            }

            @Override
            public void runOnSuccess() {
                loadFilesDatabase(false, false);
                enableGui();
                updateFeedback("Added files!");
            }
        });
    }


    public static Optional<File> getFile() {
        Stage stage = new Stage();

        File file = getImageFileChooser().showOpenDialog(stage);

        if (file != null) {
            lastFileDirectory = file.getParentFile();
        }

        return Optional.ofNullable(file);
    }

    public static List<File> getFiles() {
        Stage stage = new Stage();

        List<File> files = getImageFileChooser().showOpenMultipleDialog(stage);

        if (files != null && files.size() > 0) {
            lastFileDirectory = files.get(0).getParentFile();
        }

        if (files == null) {
            return new ArrayList<>();
        }

        return files;
    }


    private static File lastFileDirectory = new File(System.getProperty("user.dir"));

    private static FileChooser getImageFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        return fileChooser;

    }

    private void flash() {
        new Thread(() -> {
            Platform.runLater(() -> {
                feedbackLabel.setTextFill(Color.web("#ff0000", 1));
            });
            waitTime(500);
            Platform.runLater(() -> {
                feedbackLabel.setTextFill(Color.web("#000000", 1));
            });
            waitTime(500);
            Platform.runLater(() -> {
                feedbackLabel.setTextFill(Color.web("#ff0000", 1));
            });
            waitTime(500);
            Platform.runLater(() -> {
                feedbackLabel.setTextFill(Color.web("#000000", 1));
            });
        }).start();
    }

    private void waitTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateFeedback(String text) {
        if (Platform.isFxApplicationThread()) {
            feedbackLabel.setText(text);
            flash();
        } else {
            Platform.runLater(() -> {
                updateFeedback(text);
            });
        }
    }

    @FXML
    void removeFilePress(ActionEvent event) {
        if (chosenFile == null) {
            updateFeedback("Must select file first");
            return;
        }
        disableGui();
        execute(new Job() {

            @Override
            public void runNow() {
                IcfCollection icfDatabase = IcfCollection.getCollection(chosenDatabase, "");
                ImageInDatabase imageInDatabase = icfDatabase.addOrFindFile(Path.of(chosenFile));
                icfDatabase.removeFile(imageInDatabase);
            }

            @Override
            public void runOnSuccess() {
                loadFilesDatabase(false, false);
                enableGui();
                updateFeedback("Removed file");
            }
        });

    }

    @FXML
    void addDatabase(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/ksitarski/icf/gui/dbCreation.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Create database");

        DbCreation controller = loader.getController();

        Scene scene = new Scene(root, 375, 150);

        stage.setScene(scene);
        stage.setResizable(false);

        controller.setOnSuccess(s -> {
            stage.close();
            new Thread(() -> {
                if (s.trim().length() > 0 && s.trim().length() < 64) {
                    if (IcfCollection.getCollectionsNames().contains(s.trim())) {
                        Platform.runLater(() -> {
                            updateFeedback("Database with this name already exists");
                            enableGui();
                        });
                    } else {
                        IcfCollection.getOrCreateCollection(s.trim(), "");
                        Platform.runLater(() -> {
                            updateFeedback("Database \"" + s.trim() + "\" created");
                            refreshDatabases(false);
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        updateFeedback("Database name \"" + s + "\" cannot be used");
                        enableGui();
                    });
                }
            }).start();
        });

        stage.setOnCloseRequest(windowEvent -> enableGui());

        stage.show();
        disableGui();
    }


    @FXML
    void findDuplicatesPress(ActionEvent event) {
        if (chosenDatabase == null) {
            updateFeedback("Must select database first!");
            return;
        }
        disableGui();
        execute(new Job() {

            @Override
            public void runNow() {
                IcfCollection icfDatabase = IcfCollection.getCollection(chosenDatabase, "");
                ComplexImageComparator cic = algorithmSelection.getSelectionModel().getSelectedItem();
                Platform.runLater(() -> {
                    cutoffSlider.setValue(cic.getRecommendedCutoff() * 100);
                });
                imageComparingResults = new IcfEngineDispatcher().findDuplicatesInLibrary(icfDatabase, cic, new IcfSettings().setCutoffStrategy(IcfSettings.ACCEPT_ALL), getComparisonProgress());
            }

            @Override
            public void runOnSuccess() {
                updateResults();
                Platform.runLater(() -> {
                    if (imageComparingResults.getExceptions().size() > 0) {
                        updateFeedback("Failed to load: " + imageComparingResults.getExceptions().size() + " images");
                    }
                });
                moveToResults();
                enableGui();
                setProgressProcessing(1);
                setProgressLoading(1);
            }
        });
    }



    @FXML
    void findCopyOfPress(ActionEvent event) {
        if (chosenDatabase == null) {
            updateFeedback("Must select database first!");
            return;
        }
        disableGui();
        Optional<File> optionalFile = getFile();
        if (!optionalFile.isPresent()) {
            updateFeedback("File must be chosen");
            enableGui();
            return;
        }

        execute(new Job() {

            @Override
            public void runNow() {
                IcfCollection icfDatabase = IcfCollection.getCollection(chosenDatabase, "");
                ComplexImageComparator cic = algorithmSelection.getSelectionModel().getSelectedItem();
                Platform.runLater(() -> {
                    cutoffSlider.setValue(cic.getRecommendedCutoff() * 100);
                });
                imageComparingResults = null;
                try {
                    imageComparingResults = new IcfEngineDispatcher().findDuplicateOfInLibrary(icfDatabase, new LoadableImageFile(optionalFile.get().toPath()), cic, new IcfSettings().setCutoffStrategy(IcfSettings.ACCEPT_ALL), getComparisonProgress());
                } catch (AlgorithmFailureException e) {
                    Platform.runLater(() -> {
                        updateFeedback("Failed to load compared image, could not finish algorithm.");
                    });
                }
            }

            @Override
            public void runOnSuccess() {
                if (imageComparingResults != null) {
                    updateResults();
                    Platform.runLater(() -> {
                        if (imageComparingResults.getExceptions().size() > 0) {
                            updateFeedback("Failed to load: " + imageComparingResults.getExceptions().size() + " images");
                        }
                    });
                    moveToResults();
                }
                enableGui();
                setProgressProcessing(1);
                setProgressLoading(1);
            }
        });

    }

    private void moveToResults() {
        leftPaneAccordion.expandedPaneProperty().set(leftPaneAccordion.getPanes().get(2));
    }

    private void updateResults() {
        if (imageComparingResults == null) {
            return;
        }
        int index = comparisonList.getSelectionModel().getSelectedIndex();
        double cutoff = cutoffSlider.getValue() / 100.0;
        ObservableList<ComparisonListElement> dbs = FXCollections.observableArrayList();
        for (ImageComparingResult imageComparingResult : imageComparingResults.getResults()) {
            if (imageComparingResult.getEquality() >= cutoff) {
                dbs.add(new ComparisonListElement(imageComparingResult));
            }
        }
        comparisonList.setItems(dbs);
        comparisonList.getSelectionModel().select(index);
    }

    private void disableGui() {
        leftPaneAccordion.setDisable(true);
    }

    private void enableGui() {
        leftPaneAccordion.setDisable(false);
    }

    private void setProgressUnsure() {
        setProgressLoading(-1);
        setProgressProcessing(-1);
    }

    private void setProgressLoading(double value) {
        progressBar1.setProgress(value);
    }
    private void setProgressProcessing(double value) {
        progressBar.setProgress(value);
    }

    private void execute(Job job) {
        new Thread(() -> {
            job.runNow();
            Platform.runLater(job::runOnSuccess);
        }).start();
    }

    private interface Job {
        void runNow();

        void runOnSuccess();
    }

    private ComparisonProgress getComparisonProgress() {
        return new ComparisonProgress() {
            @Override
            public void updateLoading(double i) {
                Platform.runLater(() -> {
                    setProgressLoading(i);
                });
            }

            @Override
            public void updateProcessing(double i) {
                Platform.runLater(() -> {
                    setProgressProcessing(i);
                });
            }
        };
    }

}

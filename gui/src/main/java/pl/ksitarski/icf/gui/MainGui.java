package pl.ksitarski.icf.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class MainGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ImageIO.setUseCache(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/ksitarski/icf/gui/gui.fxml"));
        Parent root = loader.load();
        stage.setTitle("ICF GUI");

        GuiController controller = loader.getController();

        Scene scene = new Scene(root, 1600, 900);

        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(1200);
        stage.show();
    }
}

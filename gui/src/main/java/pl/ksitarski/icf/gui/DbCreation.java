package pl.ksitarski.icf.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DbCreation {

    @FXML
    private TextField databaseName;

    private TFunction<String> onSuccess;

    @FXML
    void createPress(ActionEvent event) {
        String name = databaseName.getText();
        System.out.println(name);
        onSuccess.run(name);
    }

    public DbCreation setOnSuccess(TFunction<String> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
}

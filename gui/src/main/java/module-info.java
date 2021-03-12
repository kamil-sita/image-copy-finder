module icf.gui {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires icf.core;
    requires icf.comparator.jimagehash;
    requires JImageHash;

    opens pl.ksitarski.icf.gui to javafx.fxml;
}
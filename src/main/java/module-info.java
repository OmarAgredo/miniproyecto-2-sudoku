/**
 * JavaFX module for the EISC Sudoku application.
 */
module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.demo.controller to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.model;
    exports com.example.demo.view;
    exports com.example.demo.controller;
}

package com.example.demo;

import com.example.demo.view.SudokuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point for the JavaFX Sudoku game.
 *
 * @author Omar Esteban Agredo
 */
public class Main extends Application {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application by creating the main Sudoku view.
     *
     * @param primaryStage main JavaFX stage
     */
    @Override
    public void start(Stage primaryStage) {
        new SudokuView(primaryStage);
    }
}

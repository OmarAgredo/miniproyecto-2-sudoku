package com.example.demo.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Loads and displays the main Sudoku JavaFX view.
 *
 * @author Omar Esteban Agredo
 */
public class SudokuView implements IView {
    private Stage primaryStage;

    /**
     * Creates the view and displays it in the provided primary stage.
     *
     * @param primaryStage main JavaFX stage
     */
    public SudokuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showView();
    }

    /**
     * Loads the Sudoku FXML file, configures the scene and shows the stage.
     */
    @Override
    public void showView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/sudoku-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setTitle("Sudoku");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Sudoku view", exception);
        }
    }

    /**
     * Closes the primary stage.
     */
    @Override
    public void deleteView() {
        primaryStage.close();
    }
}

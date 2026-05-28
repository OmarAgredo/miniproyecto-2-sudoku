package com.example.demo.controller;

import com.example.demo.model.SudokuBoard;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Handles user interaction between the Sudoku view and the Sudoku model.
 *
 * @author Omar Esteban Agredo
 */
public class SudokuController implements Initializable {
    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Label statusLabel;

    private SudokuBoard sudokuBoard;
    private TextField[][] cells;
    private Set<String> invalidCells;
    private Set<String> hintCells;

    /**
     * Initializes the Sudoku model, creates the visual board and renders the first puzzle state.
     *
     * @param location location used to resolve relative paths
     * @param resources resources used to localize the controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sudokuBoard = new SudokuBoard();
        cells = new TextField[SudokuBoard.SIZE][SudokuBoard.SIZE];
        invalidCells = new HashSet<>();
        hintCells = new HashSet<>();
        createBoardFields();
        refreshBoard();
        updateStatus("Enter numbers from 1 to 6.", StatusType.DEFAULT);
    }

    /**
     * Creates the 6x6 grid of text fields and assigns keyboard and focus behavior.
     */
    private void createBoardFields() {
        sudokuGrid.getChildren().clear();

        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefSize(42, 42);
                cell.setMinSize(42, 42);
                cell.setMaxSize(42, 42);
                cell.setAlignment(Pos.CENTER);
                cell.setFocusTraversable(true);

                final int currentRow = row;
                final int currentCol = col;

                CellEventBinder eventBinder = new DefaultCellEventBinder();
                eventBinder.bind(cell, currentRow, currentCol);

                cells[row][col] = cell;
                sudokuGrid.add(cell, col, row);
            }
        }
    }

    /**
     * Processes keyboard actions for a Sudoku cell.
     *
     * @param event key event received by the cell
     * @param row selected row
     * @param col selected column
     */
    private void handleCellKeyPressed(KeyEvent event, int row, int col) {
        if (sudokuBoard.isFixedCell(row, col)) {
            event.consume();
            return;
        }

        KeyCode code = event.getCode();

        if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            sudokuBoard.clearCell(row, col);
            invalidCells.remove(createCellKey(row, col));
            hintCells.remove(createCellKey(row, col));
            refreshBoard();
            updateStatus("Cell cleared.", StatusType.DEFAULT);
            event.consume();
            return;
        }

        int number = getNumberFromKeyCode(code);

        if (number >= 1 && number <= SudokuBoard.SIZE) {
            if (sudokuBoard.placeNumber(row, col, number)) {
                invalidCells.remove(createCellKey(row, col));
                hintCells.remove(createCellKey(row, col));
                refreshBoard();
                updateStatus("Valid move.", StatusType.VALID);
                showWinMessageIfNeeded();
            } else {
                invalidCells.add(createCellKey(row, col));
                refreshBoard();
                updateStatus("Conflict detected in the row, column or block.", StatusType.ERROR);
            }

            event.consume();
        }
    }

    /**
     * Updates every text field according to the current model state.
     */
    private void refreshBoard() {
        int[][] currentBoard = sudokuBoard.getBoardCopy();

        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                TextField cell = cells[row][col];
                int value = currentBoard[row][col];
                boolean fixed = sudokuBoard.isFixedCell(row, col);
                boolean selected = cell.isFocused();
                String cellKey = createCellKey(row, col);

                cell.setText(value == 0 ? "" : String.valueOf(value));
                cell.setEditable(!fixed);
                cell.getStyleClass().removeAll(
                        "fixed-cell",
                        "editable-cell",
                        "selected-cell",
                        "invalid-cell",
                        "hint-cell",
                        "block-top-border",
                        "block-left-border",
                        "block-right-border",
                        "block-bottom-border"
                );
                cell.getStyleClass().add(fixed ? "fixed-cell" : "editable-cell");

                applyBlockBorderStyles(cell, row, col);

                if (selected) {
                    cell.getStyleClass().add("selected-cell");
                }

                if (hintCells.contains(cellKey)) {
                    cell.getStyleClass().add("hint-cell");
                }

                if (invalidCells.contains(cellKey)) {
                    cell.getStyleClass().add("invalid-cell");
                }
            }
        }
    }

    /**
     * Places a valid hint in the first available empty cell.
     *
     * @param event action event produced by the help button
     */
    @FXML
    private void onHandleHelp(ActionEvent event) {
        int[] hint = sudokuBoard.findHint();

        if (hint == null) {
            updateStatus("No valid hint is available. Check the current board.", StatusType.ERROR);
            return;
        }

        int row = hint[0];
        int col = hint[1];
        int number = hint[2];

        sudokuBoard.placeNumber(row, col, number);
        invalidCells.remove(createCellKey(row, col));
        hintCells.add(createCellKey(row, col));
        refreshBoard();
        cells[row][col].requestFocus();
        updateStatus("Hint placed: " + number + " at row " + (row + 1) + ", column " + (col + 1) + ".", StatusType.VALID);
        showWinMessageIfNeeded();
    }

    /**
     * Restarts the current Sudoku game without generating a different puzzle.
     *
     * @param event action event produced by the restart button
     */
    @FXML
    private void onHandleRestart(ActionEvent event) {
        sudokuBoard.restartCurrentGame();
        invalidCells.clear();
        hintCells.clear();
        refreshBoard();
        updateStatus("Current game restarted.", StatusType.DEFAULT);
    }

    /**
     * Adds or removes the selected-cell style from a text field.
     *
     * @param cell text field to update
     * @param selected true when the cell has focus
     */
    private void updateSelectedStyle(TextField cell, boolean selected) {
        cell.getStyleClass().remove("selected-cell");

        if (selected) {
            cell.getStyleClass().add("selected-cell");
        }
    }

    /**
     * Converts supported digit and numpad keys to Sudoku numbers.
     *
     * @param code key code pressed by the player
     * @return number from 1 to 6, or 0 when unsupported
     */
    private int getNumberFromKeyCode(KeyCode code) {
        if (code == KeyCode.DIGIT1 || code == KeyCode.NUMPAD1) {
            return 1;
        }

        if (code == KeyCode.DIGIT2 || code == KeyCode.NUMPAD2) {
            return 2;
        }

        if (code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3) {
            return 3;
        }

        if (code == KeyCode.DIGIT4 || code == KeyCode.NUMPAD4) {
            return 4;
        }

        if (code == KeyCode.DIGIT5 || code == KeyCode.NUMPAD5) {
            return 5;
        }

        if (code == KeyCode.DIGIT6 || code == KeyCode.NUMPAD6) {
            return 6;
        }

        return 0;
    }

    /**
     * Displays a JavaFX alert with no header.
     *
     * @param alertType type of alert to display
     * @param message message shown in the alert body
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the non-blocking status message below the board.
     *
     * @param message message to display
     * @param statusType visual status category
     */
    private void updateStatus(String message, StatusType statusType) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll(
                "status-label-valid",
                "status-label-error",
                "status-label-complete"
        );

        if (statusType == StatusType.VALID) {
            statusLabel.getStyleClass().add("status-label-valid");
        }

        if (statusType == StatusType.ERROR) {
            statusLabel.getStyleClass().add("status-label-error");
        }

        if (statusType == StatusType.COMPLETE) {
            statusLabel.getStyleClass().add("status-label-complete");
        }
    }

    /**
     * Applies visual styles that emphasize the 2x3 Sudoku blocks.
     *
     * @param cell text field to style
     * @param row cell row
     * @param col cell column
     */
    private void applyBlockBorderStyles(TextField cell, int row, int col) {
        if (row % SudokuBoard.BLOCK_ROWS == 0) {
            cell.getStyleClass().add("block-top-border");
        }

        if (col % SudokuBoard.BLOCK_COLUMNS == 0) {
            cell.getStyleClass().add("block-left-border");
        }

        if (col == SudokuBoard.SIZE - 1) {
            cell.getStyleClass().add("block-right-border");
        }

        if (row == SudokuBoard.SIZE - 1) {
            cell.getStyleClass().add("block-bottom-border");
        }
    }

    /**
     * Creates a unique key for controller-side cell state collections.
     *
     * @param row cell row
     * @param col cell column
     * @return unique cell key
     */
    private String createCellKey(int row, int col) {
        return row + "-" + col;
    }

    /**
     * Shows a success dialog when the player completes the puzzle.
     */
    private void showWinMessageIfNeeded() {
        if (sudokuBoard.isGameWon()) {
            updateStatus("Puzzle completed successfully.", StatusType.COMPLETE);
            showAlert(Alert.AlertType.INFORMATION, "Congratulations, you solved the Sudoku.");
        }
    }

    /**
     * Defines the visual state used by the status label.
     *
     * @author Omar Esteban Agredo
     */
    private enum StatusType {
        DEFAULT,
        VALID,
        ERROR,
        COMPLETE
    }

    /**
     * Defines how a cell receives its event handlers.
     *
     * @author Omar Esteban Agredo
     */
    private interface CellEventBinder {
        /**
         * Binds event handlers to a Sudoku text field.
         *
         * @param cell cell text field
         * @param row cell row
         * @param col cell column
         */
        void bind(TextField cell, int row, int col);
    }

    /**
     * Default binder for keyboard, mouse and focus events.
     *
     * @author Omar Esteban Agredo
     */
    private class DefaultCellEventBinder implements CellEventBinder {
        /**
         * Binds the complete set of cell events.
         *
         * @param cell cell text field
         * @param row cell row
         * @param col cell column
         */
        @Override
        public void bind(TextField cell, int row, int col) {
            cell.addEventFilter(KeyEvent.KEY_TYPED, new CellTypedAdapter());
            cell.addEventFilter(KeyEvent.KEY_PRESSED, new CellKeyPressedAdapter(row, col));
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new CellMouseClickedAdapter(cell));
            cell.focusedProperty().addListener(new CellFocusAdapter(cell));
        }
    }

    /**
     * Adapter class for typed keyboard events.
     *
     * @author Omar Esteban Agredo
     */
    private class CellTypedAdapter implements EventHandler<KeyEvent> {
        /**
         * Consumes direct text editing.
         *
         * @param event key event to consume
         */
        @Override
        public void handle(KeyEvent event) {
            event.consume();
        }
    }

    /**
     * Adapter class for pressed keyboard events.
     *
     * @author Omar Esteban Agredo
     */
    private class CellKeyPressedAdapter implements EventHandler<KeyEvent> {
        private final int row;
        private final int col;

        /**
         * Creates a key adapter for a specific Sudoku cell.
         *
         * @param row cell row
         * @param col cell column
         */
        private CellKeyPressedAdapter(int row, int col) {
            this.row = row;
            this.col = col;
        }

        /**
         * Delegates key processing to the controller.
         *
         * @param event key event received by the cell
         */
        @Override
        public void handle(KeyEvent event) {
            handleCellKeyPressed(event, row, col);
        }
    }

    /**
     * Adapter class for mouse events.
     *
     * @author Omar Esteban Agredo
     */
    private class CellMouseClickedAdapter implements EventHandler<MouseEvent> {
        private final TextField cell;

        /**
         * Creates a mouse adapter for a text field.
         *
         * @param cell text field to focus
         */
        private CellMouseClickedAdapter(TextField cell) {
            this.cell = cell;
        }

        /**
         * Requests focus when the player clicks a cell.
         *
         * @param event mouse event received by the cell
         */
        @Override
        public void handle(MouseEvent event) {
            cell.requestFocus();
        }
    }

    /**
     * Adapter class for focus events.
     *
     * @author Omar Esteban Agredo
     */
    private class CellFocusAdapter implements javafx.beans.value.ChangeListener<Boolean> {
        private final TextField cell;

        /**
         * Creates a focus adapter for a text field.
         *
         * @param cell text field to update
         */
        private CellFocusAdapter(TextField cell) {
            this.cell = cell;
        }

        /**
         * Updates the selected visual style when focus changes.
         *
         * @param observable observed focus property
         * @param oldValue previous focus value
         * @param newValue current focus value
         */
        @Override
        public void changed(javafx.beans.value.ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            updateSelectedStyle(cell, newValue);
        }
    }
}

package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the mathematical model for a 6x6 Sudoku board.
 *
 * @author Omar Esteban Agredo
 */
public class SudokuBoard {
    /**
     * Number of rows and columns in the Sudoku board.
     */
    public static final int SIZE = 6;

    /**
     * Number of rows in each Sudoku block.
     */
    public static final int BLOCK_ROWS = 2;

    /**
     * Number of columns in each Sudoku block.
     */
    public static final int BLOCK_COLUMNS = 3;

    /**
     * Number of cells contained in each 2x3 block.
     */
    public static final int CELLS_PER_BLOCK = 6;

    private int[][] board;
    private int[][] solution;
    private int[][] fixedCells;
    private Random random;

    /**
     * Creates a Sudoku board and immediately generates a new game.
     */
    public SudokuBoard() {
        board = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
        fixedCells = new int[SIZE][SIZE];
        random = new Random();
        generateNewGame();
    }

    /**
     * Clears all board states, generates a valid solution and creates the initial fixed cells.
     */
    public void generateNewGame() {
        clearMatrix(board);
        clearMatrix(solution);
        clearMatrix(fixedCells);

        while (!generateSolution(0, 0)) {
            clearMatrix(solution);
        }

        generateInitialNumbers();
    }

    /**
     * Generates a complete valid Sudoku solution using recursive backtracking.
     *
     * @param row current row to solve
     * @param col current column to solve
     * @return true when the solution is complete
     */
    private boolean generateSolution(int row, int col) {
        if (row == SIZE) {
            return true;
        }

        int nextRow = row;
        int nextCol = col + 1;

        if (nextCol == SIZE) {
            nextRow++;
            nextCol = 0;
        }

        List<Integer> numbers = createRandomNumbers();

        for (int number : numbers) {
            if (canPlace(solution, row, col, number)) {
                solution[row][col] = number;

                if (generateSolution(nextRow, nextCol)) {
                    return true;
                }

                solution[row][col] = 0;
            }
        }

        return false;
    }

    /**
     * Creates a randomly shuffled row containing the valid numbers from 1 to 6.
     *
     * @return shuffled list containing the available numbers
     */
    private List<Integer> createRandomNumbers() {
        List<Integer> numbers = new ArrayList<>();

        for (int number = 1; number <= SIZE; number++) {
            numbers.add(number);
        }

        Collections.shuffle(numbers, random);
        return numbers;
    }

    /**
     * Checks whether a number can be placed in a matrix without breaking Sudoku rules.
     *
     * @param matrix matrix to validate
     * @param row target row
     * @param col target column
     * @param number number to place
     * @return true if the move respects row, column and block constraints
     */
    public boolean canPlace(int[][] matrix, int row, int col, int number) {
        if (!isInsideBoard(row, col) || number < 1 || number > SIZE) {
            return false;
        }

        for (int currentCol = 0; currentCol < SIZE; currentCol++) {
            if (matrix[row][currentCol] == number) {
                return false;
            }
        }

        for (int currentRow = 0; currentRow < SIZE; currentRow++) {
            if (matrix[currentRow][col] == number) {
                return false;
            }
        }

        int blockStartRow = row - row % BLOCK_ROWS;
        int blockStartCol = col - col % BLOCK_COLUMNS;

        for (int currentRow = blockStartRow; currentRow < blockStartRow + BLOCK_ROWS; currentRow++) {
            for (int currentCol = blockStartCol; currentCol < blockStartCol + BLOCK_COLUMNS; currentCol++) {
                if (matrix[currentRow][currentCol] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Verifies a player move against the current board while preserving the existing value.
     *
     * @param row target row
     * @param col target column
     * @param number number to verify
     * @return true if the move is legal and the cell is editable
     */
    public boolean verifyMove(int row, int col, int number) {
        if (!isInsideBoard(row, col) || isFixedCell(row, col) || number < 1 || number > SIZE) {
            return false;
        }

        int currentValue = board[row][col];
        board[row][col] = 0;
        boolean validMove = canPlace(board, row, col, number);
        board[row][col] = currentValue;

        return validMove;
    }

    /**
     * Places a number on the board when the move is valid.
     *
     * @param row target row
     * @param col target column
     * @param number number to place
     * @return true if the number was placed
     */
    public boolean placeNumber(int row, int col, int number) {
        if (verifyMove(row, col, number)) {
            board[row][col] = number;
            return true;
        }

        return false;
    }

    /**
     * Clears a non-fixed cell.
     *
     * @param row target row
     * @param col target column
     * @return true if the cell was cleared
     */
    public boolean clearCell(int row, int col) {
        if (!isInsideBoard(row, col) || isFixedCell(row, col)) {
            return false;
        }

        board[row][col] = 0;
        return true;
    }

    /**
     * Checks whether a cell is part of the initial puzzle hints.
     *
     * @param row target row
     * @param col target column
     * @return true if the cell is fixed
     */
    public boolean isFixedCell(int row, int col) {
        if (!isInsideBoard(row, col)) {
            return false;
        }

        return fixedCells[row][col] == 1;
    }

    /**
     * Checks whether the board has no empty cells.
     *
     * @return true if every cell contains a number
     */
    public boolean isComplete() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks whether all non-empty values currently on the board satisfy Sudoku rules.
     *
     * @return true if the current board state is valid
     */
    public boolean isBoardValid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = board[row][col];

                if (value != 0) {
                    board[row][col] = 0;

                    if (!canPlace(board, row, col, value)) {
                        board[row][col] = value;
                        return false;
                    }

                    board[row][col] = value;
                }
            }
        }

        return true;
    }

    /**
     * Checks whether the current board is complete, valid and equal to the generated solution.
     *
     * @return true if the game is won
     */
    public boolean isGameWon() {
        if (!isComplete() || !isBoardValid()) {
            return false;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != solution[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Finds a valid hint for the first empty cell that matches the current board constraints.
     *
     * @return array with row, column and value, or null when there are no empty cells
     */
    public int[] findHint() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0 && canPlace(board, row, col, solution[row][col])) {
                    return new int[] {row, col, solution[row][col]};
                }
            }
        }

        return null;
    }

    /**
     * Returns a defensive copy of the current player board.
     *
     * @return copied board matrix
     */
    public int[][] getBoardCopy() {
        return copyMatrix(board);
    }

    /**
     * Returns a defensive copy of the fixed-cell matrix.
     *
     * @return copied fixed-cell matrix
     */
    public int[][] getFixedCellsCopy() {
        return copyMatrix(fixedCells);
    }

    /**
     * Returns a defensive copy of the generated solution.
     *
     * @return copied solution matrix
     */
    public int[][] getSolutionCopy() {
        return copyMatrix(solution);
    }

    /**
     * Places exactly two fixed numbers in each 2x3 block.
     */
    private void generateInitialNumbers() {
        for (int blockRow = 0; blockRow < SIZE; blockRow += BLOCK_ROWS) {
            for (int blockCol = 0; blockCol < SIZE; blockCol += BLOCK_COLUMNS) {
                List<CellPosition> positions = new ArrayList<>();

                for (int row = blockRow; row < blockRow + BLOCK_ROWS; row++) {
                    for (int col = blockCol; col < blockCol + BLOCK_COLUMNS; col++) {
                        positions.add(new CellPosition(row, col));
                    }
                }

                Collections.shuffle(positions, random);

                for (int i = 0; i < 2; i++) {
                    CellPosition position = positions.get(i);
                    board[position.row][position.col] = solution[position.row][position.col];
                    fixedCells[position.row][position.col] = 1;
                }
            }
        }
    }

    /**
     * Sets every matrix value to zero.
     *
     * @param matrix matrix to clear
     */
    private void clearMatrix(int[][] matrix) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                matrix[row][col] = 0;
            }
        }
    }

    /**
     * Creates a deep copy of a matrix.
     *
     * @param source source matrix
     * @return copied matrix
     */
    private int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][source[0].length];

        for (int row = 0; row < source.length; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, source[row].length);
        }

        return copy;
    }

    /**
     * Checks whether a coordinate belongs to the Sudoku board.
     *
     * @param row row to validate
     * @param col column to validate
     * @return true if the coordinate is inside the board
     */
    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Represents a coordinate inside the Sudoku board.
     *
     * @author Omar Esteban Agredo
     */
    private static class CellPosition {
        private final int row;
        private final int col;

        /**
         * Creates a board coordinate.
         *
         * @param row coordinate row
         * @param col coordinate column
         */
        private CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}

# Sudoku OEA

JavaFX 6x6 Sudoku game built with FXML, CSS and a Model-View-Controller architecture.

## Author

Omar Esteban Agredo

## Requirements

- JDK 17
- Maven Wrapper included in the project
- JavaFX dependencies managed by Maven

## How to Run

```powershell
$env:JAVA_HOME='C:\Users\omara\.jdks\corretto-17.0.19'
.\mvnw.cmd javafx:run
```

## How to Compile

```powershell
$env:JAVA_HOME='C:\Users\omara\.jdks\corretto-17.0.19'
.\mvnw.cmd compile
```

## Features

- 6x6 Sudoku board.
- Six 2x3 blocks.
- Randomly generated complete solution.
- Exactly two fixed numbers per block.
- Fixed cells cannot be edited or deleted.
- Keyboard input with digits from 1 to 6.
- Delete and Backspace support for editable cells.
- Real-time validation for row, column and block conflicts.
- Visual highlighting for fixed, selected, invalid and hint cells.
- Help button that fills a valid empty cell.
- Restart button that restores the current puzzle to its initial state.
- Non-blocking status messages for normal feedback.
- MVC separation with model, view and controller packages.
- JavaFX event handling with interfaces, adapter-style classes and inner classes.

## Architecture

The project follows the Model-View-Controller pattern:

- `com.example.demo.model`: contains the Sudoku board state, solution generation, movement validation and win detection.
- `com.example.demo.view`: loads the FXML scene and controls the JavaFX stage.
- `com.example.demo.controller`: connects user actions with the model and updates the visual board.

This separation keeps the Sudoku rules independent from the JavaFX interface and reduces coupling between the game logic and the UI.

## Event Handling

The controller handles multiple JavaFX events:

- `KEY_PRESSED`: places numbers from 1 to 6 and handles Delete or Backspace.
- `KEY_TYPED`: consumes direct text editing to keep input controlled by the model.
- `MOUSE_CLICKED`: focuses the selected cell.
- Focus changes: update the selected-cell visual state.
- Button actions: provide hints and restart the current puzzle.

The implementation uses an internal `CellEventBinder` interface and adapter-style inner classes for keyboard, mouse and focus events.

## Data Structures

Besides arrays used for the board matrices, the project uses additional data structures:

- `ArrayList` and `List<Integer>` to generate shuffled candidate numbers during solution construction.
- `ArrayList<CellPosition>` to shuffle block positions and choose exactly two fixed cells per block.
- `HashSet<String>` to track invalid cells and hint cells in the controller.

The `ArrayList<CellPosition>` structure is part of the Sudoku board construction logic.

## UX Heuristics Applied

- Visibility of system status: the status label reports valid moves, conflicts, hints and completion.
- Error prevention: fixed cells cannot be edited and typed text is controlled by key events.
- Error recognition and recovery: invalid moves are highlighted without interrupting the player with repeated dialogs.
- Consistency and standards: fixed, selected, hint and conflict states use consistent colors.
- User control and freedom: players can clear editable cells and restart the current puzzle.
- Recognition rather than recall: the legend explains the visual meaning of board states.

## Generate JavaDoc

```powershell
$env:JAVA_HOME='C:\Users\omara\.jdks\corretto-17.0.19'
.\mvnw.cmd javadoc:javadoc
```

The generated documentation is created in:

```text
target/reports/apidocs
```

## Version Control

The repository is configured for GitHub. The final delivery should include:

- Public GitHub repository.
- Complete README.
- Version tag, for example `v1.0.0`.
- Pull request history if the project workflow requires it.

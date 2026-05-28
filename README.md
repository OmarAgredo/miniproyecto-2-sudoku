# EISC Sudoku

JavaFX 6x6 Sudoku game built with a strict Model-View-Controller structure.

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
- Unlimited help button that fills a valid empty cell.
- MVC separation with model, view and controller packages.
- JavaFX event handling with interfaces, adapter-style classes and inner classes.
- Additional data structures integrated into board construction and UI state management.

## Generate JavaDoc

```powershell
$env:JAVA_HOME='C:\Users\omara\.jdks\corretto-17.0.19'
.\mvnw.cmd javadoc:javadoc
```

The generated documentation is created in:

```text
target/site/apidocs
```


package com.puzzle;

import java.io.File;
import java.io.IOException;

import com.puzzle.core.PuzzleInput;
import com.puzzle.core.PuzzleSolver;
import com.puzzle.utils.FileHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class PrimaryController {
    @FXML private Button chooseFileButton;
    @FXML private Button solveButton;
    @FXML private Button saveImageButton;
    @FXML private Button saveTextButton;
    @FXML private Label fileLabel;  
    @FXML private GridPane boardGrid;
    @FXML private TextArea outputArea;
    @FXML private ProgressIndicator loadingIndicator;
    private File selectedFile;
    private PuzzleSolver solver;
    private int N, M;
    private static final int CELL_SIZE = 40;
    private boolean solved = false;

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Puzzle File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
            solveButton.setDisable(false);
            clearBoard();
        }
    }

    private void clearBoard() {
        boardGrid.getChildren().clear();
        outputArea.clear();
        saveImageButton.setDisable(true);
        saveTextButton.setDisable(true);
    }

    @FXML
    private void solvePuzzle() {
        Platform.runLater(() -> {
            loadingIndicator.setVisible(true);
            solveButton.setDisable(true);
        });
        new Thread(() -> {
            try {
                PuzzleInput input = FileHandler.readInputFile(selectedFile);
                solver = new PuzzleSolver(input.N, input.M, input.pieces, input.grid);
                N = input.N;
                M = input.M;

                Platform.runLater(() -> {
                    outputArea.clear();
                });
                
                boolean solved = solver.solve();
                long executionTime = solver.getExecutionTime();
                long iterations = solver.getIterations();

            
                Platform.runLater(() -> {
                    
                    outputArea.appendText("\nExecution time: " + executionTime + " ms\n");
                    outputArea.appendText("Iterations: " + iterations + "");

                    if (solved) {
                        outputArea.appendText("\nBoard state:\n");
                        outputArea.appendText(solver.getBoardState());
                        updateBoard(solver.getBoardState());
                        saveImageButton.setDisable(false);
                        saveTextButton.setDisable(false);
                    } else {
                        outputArea.appendText("\nNo solution exists!\n");
                        saveTextButton.setDisable(false);  
                        saveImageButton.setDisable(true);
                    }

                    
                    loadingIndicator.setVisible(false);
                    solveButton.setDisable(false);
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    outputArea.appendText("Error: " + e.getMessage() + "\n");
                    loadingIndicator.setVisible(false);
                    solveButton.setDisable(false);
                });
            }
        }).start();
    }
    private void updateBoard(String boardState) {
        boardGrid.getChildren().clear();
        String[] rows = boardState.split("\n");
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                StackPane cellStack = new StackPane();  
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                char c = rows[i].charAt(j);
                
                if (c != '.' && c != ' ') {
                    cell.setFill(FileHandler.COLORS[c - 'A']);
                    Text text = new Text(String.valueOf(c));
                    text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    text.setFill(Color.BLACK);  
                    text.setStroke(Color.WHITE);
                    text.setStrokeWidth(0.5);
                    cellStack.getChildren().addAll(cell, text);
                } else {
                    cell.setFill(Color.WHITE);
                    cellStack.getChildren().add(cell);
                }
                
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(1);
               
                boardGrid.add(cellStack, j, i);
            }
        }
    }
    @FXML
    private void saveImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
            );
            fileChooser.setInitialFileName("solution_image.png");
    
            File file = fileChooser.showSaveDialog(saveImageButton.getScene().getWindow());
            if (file != null) {
                FileHandler.saveImage(file, 
                                  solver.getBoardState(), 
                                  N, M,
                                  solver.getExecutionTime(),
                                  solver.getIterations());
                outputArea.appendText("Solution saved as image: " + file.getName() + "\n");
            }
        } catch (Exception e) {
            outputArea.appendText("Error saving image: " + e.getMessage() + "\n");
        }
    }
    
    @FXML
    private void saveText() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Text");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            fileChooser.setInitialFileName("solution_text.txt");
    
            File file = fileChooser.showSaveDialog(saveTextButton.getScene().getWindow());
            if (file != null) {
                FileHandler.saveText(file,
                                 solver.getBoardState(),
                                 solver.getExecutionTime(),
                                 solver.getIterations(),
                                 solved);
                outputArea.appendText("Solution saved as text: " + file.getName() + "\n");
            }
        } catch (IOException e) {
            outputArea.appendText("Error saving text: " + e.getMessage() + "\n");
        }
    }
    
}
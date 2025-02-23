package com.puzzle.utils;

import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.puzzle.core.Piece;
import com.puzzle.core.PuzzleInput;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FileHandler {
    public static final Color[] COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
        Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW,
        Color.MAROON, Color.DARKGREEN, Color.NAVY, Color.PURPLE,
        Color.OLIVE, Color.TEAL, Color.LIGHTCORAL, Color.DARKORANGE,
        Color.ORCHID, Color.POWDERBLUE, Color.BLANCHEDALMOND, Color.LIGHTBLUE,
        Color.PLUM, Color.LIGHTGREEN, Color.LIGHTSALMON, Color.MEDIUMAQUAMARINE,
        Color.SKYBLUE, Color.PALEVIOLETRED
    };


    public static PuzzleInput readInputFile(File inputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                throw new IOException("Format error: Missing N M P line");
            }
            
            String[] dims = firstLine.trim().split("\\s+");
            if (dims.length != 3) {
                throw new IOException("Format error: N M P line must contain exactly 3 numbers");   
            }
            
            try {
                int N = Integer.parseInt(dims[0]);
                int M = Integer.parseInt(dims[1]);
                int P = Integer.parseInt(dims[2]);
                
                if (N <= 0 || M <= 0 || P <= 0) {
                    throw new IOException("Invalid input: N, M, P must be positive");
                }

                String mode = reader.readLine().trim();
                char[][] grid = new char[N][M];
                
                if (mode.equals("DEFAULT")) {
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < M; j++) {
                            grid[i][j] = '.';

                        }
                    }
                } 
                // else if (mode.equals("CUSTOM")) {
                //     for (int i = 0; i < N; i++) {
                //         String line = reader.readLine();
                //         if (line == null || line.length() != M) {
                //             throw new IOException("Invalid custom configuration format");
                //         }
                //         for (int j = 0; j < M; j++) {
                //             grid[i][j] = line.charAt(j) == 'X' ? '.' : ' ';
                //         }
                //     }
                // }
                else {
                    throw new IOException("Invalid mode");
                }

                List<Piece> pieces = new ArrayList<>();
                List<String> currentLines = new ArrayList<>();
                String line;
                char currentId = '\0';
                int maxWidth = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        char pieceId = line.charAt(0);

                        int currentLineWidth = 0;
                        for (char c : line.toCharArray()) {
                            if (c == pieceId) {  
                                currentLineWidth++;
                            }
                        }
                        maxWidth = Math.max(maxWidth, currentLineWidth);
                        
                        if (currentId == '\0') {
                            currentId = pieceId;
                        } else if (pieceId != currentId) {
                            if (currentLines.size() > N || maxWidth > M) {
                                throw new IOException("Piece " + pieceId + " size exceeds board dimensions");
                            }
                            
                            pieces.add(Piece.createFromLines(currentLines, currentId));
                            currentLines = new ArrayList<>();
                            currentId = pieceId;
                            maxWidth = currentLineWidth;  
                        }
                        
                       
                        for (char c : line.toCharArray()) {
                            if (c != ' ' && c != currentId) {
                                throw new IOException("Invalid piece format: mixed Letters in same piece");
                            }
                        }
                        
                        currentLines.add(line);
                    }
                }
      
                if (!currentLines.isEmpty()) {
                    int height = currentLines.size();
                    int maxDimension = Math.max(height, maxWidth);
                    if (maxDimension > Math.max(N, M)) {
                        throw new IOException("Piece " + currentId +  " size exceeds board dimensions");
                    }
                    
                    pieces.add(Piece.createFromLines(currentLines, currentId));
                }
                if (pieces.size() != P) {
                    throw new IOException("Number of pieces (" + pieces.size() + ") does not match P (" + P + ")");
                }

                Set<Character> uniqueIds = new HashSet<>();
                for (Piece piece : pieces) {
                    if (!uniqueIds.add(piece.getId())) {
                        throw new IOException("Duplicate piece ID found: " + piece.getId());
                    }
                }    
                return new PuzzleInput(N, M, P, mode, pieces,grid);
                
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number format in N M P line");
            }
        }
    }
    public static WritableImage createSolutionImage(String boardState, int N, int M, long executionTime, long iterations) {
        double CELL_SIZE = 50;
        double extraHeight = 60;
        
        Canvas canvas = new Canvas(M * CELL_SIZE, N * CELL_SIZE + extraHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
    
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    
        String[] lines = boardState.split("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                char c = lines[i].charAt(j);
                if (c != ' ' && c != '.') {
                    gc.setFill(COLORS[c - 'A']);
                    gc.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
                
                gc.setStroke(Color.BLACK);
                gc.strokeRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                
                if (c != ' ' && c != '.') {
                    gc.setFill(Color.BLACK);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    gc.fillText(String.valueOf(c), 
                        j * CELL_SIZE + CELL_SIZE/3, 
                        i * CELL_SIZE + 2*CELL_SIZE/3);
                }
            }
        }

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        gc.fillText("Execution time: " + executionTime + " ms", 
                   10, N * CELL_SIZE + 20);
        gc.fillText("Iterations: " + iterations, 
                   10, N * CELL_SIZE + 40);

        WritableImage image = new WritableImage((int)canvas.getWidth(), 
                                              (int)canvas.getHeight());
        canvas.snapshot(null, image);
        return image;
    }
    
    public static void saveText(File outputFile, String boardState, long executionTime, long iterations) throws IOException {
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Solution:");
            writer.println(boardState);
            writer.println("Execution time: " + executionTime + " ms");
            writer.println("Iterations: " + iterations);
        }
    }

    public static void saveImage(File outputFile, String boardState, int N, int M, long executionTime, long iterations) {
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        WritableImage image = createSolutionImage(boardState, N, M, executionTime, iterations);
        try {
            WritableImage writableImage = image;  
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", outputFile); 
        } catch (IOException ex) {
            System.out.println("Error saving image: " + ex.getMessage());
        }
    }


}
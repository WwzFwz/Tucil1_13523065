package com.puzzle.core;
import java.util.ArrayList;
import java.util.List;

public class PuzzleSolver {
    private Board board;
    private List<Piece> pieces;
    private long iterations;
    private long startTime;
    private long endTime;
 

    public PuzzleSolver(int rows, int cols, List<Piece> pieces, char [][] initialGrid) {
        this.board = new Board(rows, cols, initialGrid);
        this.pieces = pieces;
        this.iterations = 0;

    }

    public boolean solve() {
        startTime = System.currentTimeMillis();
        return solvePuzzle(0);
    }
    private  boolean solvePuzzle(int pieceIndex) {
        if (pieceIndex >= pieces.size()) {
            endTime = System.currentTimeMillis();
            return board.isFull();
        }

        Piece piece = pieces.get(pieceIndex);
        Piece currentPiece = piece;
        List<Point> positions = new ArrayList<>(board.getEmptyPoints());    
        for (int i = 0; i < 2; i++) { 
            for (int r = 0; r < 4; r++) { 
                for (Point p : positions) {  
                    iterations++;  
                    if (board.canPlacePiece(currentPiece, p.x, p.y)) {
                        board.placePiece(currentPiece, p.x, p.y);
                        if (solvePuzzle(pieceIndex + 1)) {
                            return true;
                        }
                        board.removePiece(currentPiece, p.x, p.y);
                    }
                }
                currentPiece = currentPiece.rotate();
            }
            currentPiece = piece.mirror();
        }
        
        return false;
    }

    public long getExecutionTime() {
        return endTime - startTime;
    }

    public long getIterations() {
        return iterations;
    }
    public String getBoardState() {
        return board.boardToString();
    }

    public int getPuzzleRows() {
        return board.getRows();
    }
    
    public int getPuzzleCols() {
        return board.getCols();
    }

}

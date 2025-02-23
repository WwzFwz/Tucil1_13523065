package com.puzzle.core;
import java.util.List;

public class PuzzleInput {
    public final int N, M, P;
    public final String mode;
    public final List<Piece> pieces;
    public final char[][] grid; 

    public PuzzleInput(int N, int M, int P, String mode, List<Piece> pieces, char[][] grid) {
        this.N = N;
        this.M = M;
        this.P = P;
        this.mode = mode;
        this.pieces = pieces;
        this.grid = grid;
    }
}
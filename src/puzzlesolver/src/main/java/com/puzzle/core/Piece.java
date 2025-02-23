package com.puzzle.core;
import java.util.ArrayList;
import java.util.List;
public class Piece{
    private List<Point> points;
    private char id;
    public Piece(List<Point> points, char id) {
        this.points = points;
        this.id = id;
    }
    public static Piece createFromLines(List<String> lines, char id) {
        List<Point> points = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == id) {
                    points.add(new Point(x, y));
                }
            }
        }
        return new Piece(points, id);
    }
    public Piece rotate(){
        List<Point> rotatedPoint = new ArrayList<>();
        for(Point p : points){
            rotatedPoint.add(new Point(-p.y,p.x));
        }
        return new Piece(rotatedPoint,id);
    }
    public Piece mirror(){
        List<Point> mirroredPoint = new ArrayList<>();
        for(Point p : points){
            mirroredPoint.add(new Point(-p.x,p.y));
        }
        return new Piece (mirroredPoint,id);
    }

    public List<Point> getPointsAt(int a, int b) {
        List<Point> translatedPoints = new ArrayList<>();
        for (Point p : points) {
            translatedPoints.add(p.translate(a, b));
        }
        return translatedPoints;
    }
    public char getId() { return id; }

}
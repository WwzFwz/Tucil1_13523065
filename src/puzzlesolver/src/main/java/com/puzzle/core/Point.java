package com.puzzle.core;

public class Point{
    public int x;
    public int y;

    public Point(int x,int y){
        this.x = x ;
        this.y = y;
    }
    public Point translate(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}   
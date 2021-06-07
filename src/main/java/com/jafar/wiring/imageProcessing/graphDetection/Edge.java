package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class Edge {
    private int x;
    private int y;
    private ArrayList<Character> directions = new ArrayList<>();
    private Vertice parentVertice;
    private boolean door = false;
    private boolean paired = false;

    public Edge(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Edge(int x, int y, char direction, Vertice parentVertice) {
        this.x = x;
        this.y = y;
        this.directions.add(direction);
        this.parentVertice = parentVertice;
    }

    public Edge(int x, int y, Vertice parentVertice, boolean door) {
        this.x = x;
        this.y = y;
        this.parentVertice = parentVertice;
        this.door = door;
    }

    public Edge(int x, int y, Vertice parentVertice, char direction, boolean door) {
        this.x = x;
        this.y = y;
        this.parentVertice = parentVertice;
        this.door = door;
        this.directions.add(direction);
    }

    public boolean isInsideEdge (int pixelX, int pixelY, int radius) {
        return (pixelX > x - radius) && (pixelX < x + radius) && (pixelY < y + radius) && (pixelY > y - radius);
    }

    public boolean containsDirection(char direction) {
        return directions.contains(direction);
    }

    public boolean addDirection(char direction) {
        if(!directions.contains(direction)) {
            directions.add(direction);
            return true;
        }
        return false;
    }

    public boolean addDirections(ArrayList<Character> newDirections) {
        directions.addAll(newDirections);
        return true;
    }//end addDirections

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Character> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<Character> directions) {
        this.directions = directions;
    }

    public Vertice getParentVertice() {
        return parentVertice;
    }

    public void setParentVertice(Vertice parentVertice) {
        this.parentVertice = parentVertice;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "x=" + x +
                ", y=" + y +
                ", directions=" + directions +
                ", door=" + door +
                '}' + "   parentVertice=" + (parentVertice == null ? "null" : parentVertice.getId());
    }
}

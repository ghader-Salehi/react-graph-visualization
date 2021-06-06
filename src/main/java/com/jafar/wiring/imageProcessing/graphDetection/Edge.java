package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class Edge {
    private int x;
    private int y;
    private ArrayList<Character> directions = new ArrayList<>();
    private String type;
    private Vertice parentVertice;

    public Edge(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Edge(int x, int y, char direction) {
        this.x = x;
        this.y = y;
        directions.add(direction);
    }

    public Edge(int x, int y, char direction, Vertice parentVertice) {
        this.x = x;
        this.y = y;
        this.directions.add(direction);
        this.parentVertice = parentVertice;
    }

    public boolean isInsideEdge (int pixelX, int pixelY, int radius) {
        return (pixelX > x - radius) && (pixelX < x + radius) && (pixelY < y + radius) && (pixelY > y - radius);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vertice getParentVertice() {
        return parentVertice;
    }

    public void setParentVertice(Vertice parentVertice) {
        this.parentVertice = parentVertice;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "x=" + x +
                ", y=" + y +
                '}' + "   parentVertice=" + (parentVertice == null ? "null" : parentVertice.getId());
    }
}

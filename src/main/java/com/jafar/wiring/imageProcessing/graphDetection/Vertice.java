package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class Vertice {
    private int x;
    private int y;
    private int top;
    private int left;
    private String id;
    private String type;
    private boolean hasElectricity;
    public final ArrayList<Vertice> adj = new ArrayList<>();


    public Vertice(int x, int y, String id, String type, boolean hasElectricity) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.type = type;
        this.hasElectricity = hasElectricity;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasElectricity() {
        return hasElectricity;
    }

    public void setHasElectricity(boolean hasElectricity) {
        this.hasElectricity = hasElectricity;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setTopLeft(int top, int left) {
        this.top = top;
        this.left = left;
    }

    @Override
    public String toString() {
        return "Vertice{" +
                "x=" + x +
                ", y=" + y +
                ", top=" + top +
                ", left=" + left +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", hasElectricity=" + hasElectricity +
                '}';
    }
}

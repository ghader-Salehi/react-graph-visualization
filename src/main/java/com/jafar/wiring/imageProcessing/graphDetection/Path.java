package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class Path {
    private int start;
    private int destination;
    private int distance;
    public ArrayList<Vertice> paths = new ArrayList<>();
    public ArrayList<Integer> pathIndexes = new ArrayList<>();

    public Path() {
    }

    public Path(int start, int destination) {
        this.start = start;
        this.destination = destination;
    }

    public Path(int start, int destination, int distance) {
        this.start = start;
        this.destination = destination;
        this.distance = distance;
    }

    public Path(int start, int destination, int distance, ArrayList<Vertice> vertices, ArrayList<Integer> pathIds) {
        this.start = start;
        this.destination = destination;
        this.distance = distance;
        this.pathIndexes = pathIds;
        for(int pathId : pathIds) {
            paths.add(vertices.get(pathId));
        }
    }

    public void changePath(int start, int destination, int distance, ArrayList<Vertice> vertices, ArrayList<Integer> pathIds) {
        this.start = start;
        this.destination = destination;
        this.distance = distance;
        this.pathIndexes = pathIds;
        paths.clear();
        for(int pathId : pathIds) {
            paths.add(vertices.get(pathId));
        }
    }//end resetPaths


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return start + " --> " + destination + " : " + distance + " : " + pathIndexes + "\n";
    }
}

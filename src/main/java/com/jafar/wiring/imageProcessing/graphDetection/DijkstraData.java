package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class DijkstraData {
    private int start;
    public ArrayList<Integer> destinations = new ArrayList<>();
    public ArrayList<Integer> distances = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> pathToDestination = new ArrayList<>();

    public DijkstraData(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void printOutData() {
        System.out.println("Dijkstra data. From " + start);
        for(int i=0;i<destinations.size();i++) {
            System.out.print(destinations.get(i) + "   " + distances.get(i) + "   " + pathToDestination.get(i) + "\n");
        }
    }

    @Override
    public String toString() {
        return "DijkstraData{" +
                "start=" + start +
                ", destinations=" + destinations +
                ", distances=" + distances +
                ", pathToDestination=" + pathToDestination +
                '}';
    }
}

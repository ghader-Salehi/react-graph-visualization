package com.jafar.wiring.models;

import com.jafar.wiring.imageProcessing.graphDetection.Path;
import com.jafar.wiring.imageProcessing.graphDetection.Pixel;
import com.jafar.wiring.imageProcessing.graphDetection.Vertice;

import java.util.ArrayList;

public class DatasetEndpoint {
    private int edges;
    private int boxes;
    private Pixel firstBlackPixel;
    ArrayList<Vertice> vertices = new ArrayList<>();
    ArrayList<Path> paths = new ArrayList<>();

    public DatasetEndpoint(int edges, int boxes, Pixel firstBlackPixel, ArrayList<Vertice> vertices, ArrayList<Path> paths) {
        // json minimizing
        for(Vertice vertice : vertices)
            vertice.adj.clear();
        for(Path path : paths)
            for(Vertice vertice : path.paths)
                vertice.adj.clear();

        this.edges = edges;
        this.boxes = boxes;
        this.firstBlackPixel = firstBlackPixel;
        this.vertices = vertices;
        this.paths = paths;
    }

    public int getEdges() {
        return edges;
    }

    public void setEdges(int edges) {
        this.edges = edges;
    }

    public int getBoxes() {
        return boxes;
    }

    public void setBoxes(int boxes) {
        this.boxes = boxes;
    }

    public Pixel getFirstBlackPixel() {
        return firstBlackPixel;
    }

    public void setFirstBlackPixel(Pixel firstBlackPixel) {
        this.firstBlackPixel = firstBlackPixel;
    }

    public ArrayList<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
    }
}

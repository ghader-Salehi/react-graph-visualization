package com.jafar.wiring.imageProcessing.graphDetection;

import java.util.ArrayList;

public class EdgeManager {
    ArrayList<Edge> edges = new ArrayList<>();

    public boolean addEdge(Edge edge) {
        if(edges.size() == 0) {
            edges.add(edge);
            return true;
        }

        for(Edge curEdge : edges) {

        }

        return false;
    }//end addEdge
}//end EdgeManager


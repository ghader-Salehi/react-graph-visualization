package com.jafar.wiring.custom;

import com.jafar.wiring.imageProcessing.dijkstra.Dijkstra;
import com.jafar.wiring.imageProcessing.graphDetection.DijkstraData;
import com.jafar.wiring.imageProcessing.graphDetection.Path;
import com.jafar.wiring.models.CustomGraphLink;
import com.jafar.wiring.models.CustomGraphNode;
import com.sun.deploy.xml.XMLable;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;

public class Processor {
    private ArrayList<CustomGraphLink> LINKS;
    private ArrayList<CustomGraphNode> NODES;
    private ArrayList<ArrayList<Integer>> ADJ = new ArrayList<>();

    public Processor(ArrayList<CustomGraphLink> links, ArrayList<CustomGraphNode> nodes) {
        this.LINKS = links;
        this.NODES = nodes;
        createAdjMatrix();
        findPaths();
    }

    public ArrayList<CustomGraphLink> getLINKS() {
        return this.LINKS;
    }

    public void printData() {
        for (CustomGraphNode node : NODES)
            System.out.println(node.getId() + " " + node.getColor());

        for (CustomGraphLink link : LINKS)
            System.out.println(link.getSource() + " , " + link.getTarget() + " , " + link.getLabel() + " , " + link.getColor());

        System.out.println("\nADJ:  ");
        for (ArrayList<Integer> row : ADJ) {
            System.out.println();
            for (int item : row)
                System.out.print(item);
        }
    }//end printData

    private void findPaths() {
        Dijkstra dijkstra = new Dijkstra();

        // 1. between switches longest one gets selected
        for (int j = 0; j < NODES.size(); j++) {
            DijkstraData dijkstraData = new DijkstraData(j);
            // 2. closest switch to electricity gets selected
            if (NODES.get(j).getId().startsWith("k")) {
                // get switch paths to electricity
                dijkstraData = dijkstra.dijkstra(ADJ, j);
                // choose closest to electricity
                int switchShortPathIndex = getShortestPathIndex(dijkstraData);
                updateLinksToConnectedWire(dijkstraData.pathToDestination.get(switchShortPathIndex));
            }
        }//end for
    }//end findPaths

    private void updateLinksToConnectedWire(ArrayList<Integer> pathIndexes) {
        for (int i = 0; i < pathIndexes.size() - 1; i++) {
            String curID = NODES.get(pathIndexes.get(i)).getId();
            String nextID = NODES.get(pathIndexes.get(i + 1)).getId();
            for (CustomGraphLink link : LINKS) {
                if ((link.getSource().equals(curID) && link.getTarget().equals(nextID)) || ((link.getSource().equals(nextID) && link.getTarget().equals(curID)))) {
                    link.setColor("red");
                }
            }
        }//end for
    }//end updateLinks

    private int getShortestPathIndex(DijkstraData dijkstraData) {
        int switchShortPath = Integer.MAX_VALUE;
        int switchShortPathIndex = -1;
        for (int i = 0; i < dijkstraData.destinations.size(); i++) {
            int destination = dijkstraData.destinations.get(i);
            int distance = dijkstraData.distances.get(i);
            if (NODES.get(destination).getId().equals("Source")) { // TO POWER
                if (switchShortPathIndex == -1) {
                    switchShortPath = dijkstraData.distances.get(i);
                    switchShortPathIndex = i;
                }
                else if (dijkstraData.distances.get(i) < switchShortPath) {
                    switchShortPath = dijkstraData.distances.get(i);
                    switchShortPathIndex = i;
                }
            }
        }//end for
        return switchShortPathIndex;
    }//end getShortestPath

    private void createAdjMatrix() {
        int adjSize = NODES.size();
        // initialize
        for (int i = 0; i < adjSize; i++) {
            ADJ.add(new ArrayList<>());
            for (int j = 0; j < adjSize; j++)
                ADJ.get(i).add(0);
        }//end for

        // setup real values
        for (int i = 0; i < LINKS.size(); i++) {
            int sourceIndex = getNodeIndex(LINKS.get(i).getSource());
            int targetIndex = getNodeIndex(LINKS.get(i).getTarget());
            int label = Integer.parseInt(LINKS.get(i).getLabel());
            ADJ.get(sourceIndex).set(targetIndex, label);
            ADJ.get(targetIndex).set(sourceIndex, label);
        }//end for


    }//end createAdjMatrix

    private int getNodeIndex(String id) {
        for (int i = 0; i < NODES.size(); i++) {
            if (id.equals(NODES.get(i).getId()))
                return i;
        }
        return -1;
    }//end getNodeIndex


}

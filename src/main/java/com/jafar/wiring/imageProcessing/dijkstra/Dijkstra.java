package com.jafar.wiring.imageProcessing.dijkstra;

import com.jafar.wiring.imageProcessing.graphDetection.DijkstraData;

import java.util.ArrayList;

public class Dijkstra {

    private static final int NO_PARENT = -1;

    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    public static DijkstraData dijkstra(ArrayList<ArrayList<Integer>> adjacencyMatrix,
                                        int startVertex) {
        DijkstraData dijkstraData = new DijkstraData(startVertex); // result
        int nVertices = adjacencyMatrix.get(0).size();

        // shortestDistances[i] will hold the
        // shortest distance from src to i
        int[] shortestDistances = new int[nVertices];

        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices];

        // Initialize all distances as
        // INFINITE and added[] as false
        for (int vertexIndex = 0; vertexIndex < nVertices;
             vertexIndex++) {
            shortestDistances[vertexIndex] = Integer.MAX_VALUE;
            added[vertexIndex] = false;
        }

        // Distance of source vertex from
        // itself is always 0
        shortestDistances[startVertex] = 0;

        // Parent array to store shortest
        // path tree
        int[] parents = new int[nVertices];

        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;

        // Find shortest path for all
        // vertices
        for (int i = 1; i < nVertices; i++) {

            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                if (!added[vertexIndex] &&
                        shortestDistances[vertexIndex] <
                                shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }

            // Mark the picked vertex as
            // processed
            if(nearestVertex == -1)
                continue;

            added[nearestVertex] = true;

            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                int edgeDistance = adjacencyMatrix.get(nearestVertex).get(vertexIndex);

                if (edgeDistance > 0
                        && ((shortestDistance + edgeDistance) <
                        shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

        printSolution(startVertex, shortestDistances, parents, dijkstraData);
        return dijkstraData;
    }

    // A utility function to print
    // the constructed distances
    // array and shortest paths
    private static void printSolution(int startVertex,
                                      int[] distances,
                                      int[] parents, DijkstraData dijkstraData) {
        int nVertices = distances.length;
//        System.out.print("Vertex\t Distance\tPath");

        for (int vertexIndex = 0;
             vertexIndex < nVertices;
             vertexIndex++) {
            if (vertexIndex != startVertex) {
//                System.out.print("\n" + startVertex + " -> ");
//                System.out.print(vertexIndex + " \t\t ");
//                System.out.print(distances[vertexIndex] + "\t\t");
                // store data to dijkstraData
                dijkstraData.destinations.add(vertexIndex);
                dijkstraData.distances.add(distances[vertexIndex]);
                dijkstraData.pathToDestination.add(new ArrayList<>());
                printPath(vertexIndex, parents, dijkstraData);
            }
        }
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private static void printPath(int currentVertex,
                                  int[] parents, DijkstraData dijkstraData) {

        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT) {
            return;
        }
        printPath(parents[currentVertex], parents, dijkstraData);
//        System.out.print(currentVertex + " ");
        dijkstraData.pathToDestination.get(dijkstraData.pathToDestination.size() - 1).add(currentVertex);
    }
}//end class

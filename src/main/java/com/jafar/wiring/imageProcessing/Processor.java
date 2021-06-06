package com.jafar.wiring.imageProcessing;

import com.jafar.wiring.imageProcessing.dijkstra.Dijkstra;
import com.jafar.wiring.imageProcessing.graphDetection.Vertice;
import com.jafar.wiring.imageProcessing.graphDetection.*;
import com.jafar.wiring.models.DatasetEndpoint;
import edu.princeton.cs.algs4.GrayscalePicture;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Processor {
    // utilities
    private static final int WALL_SIZE = 5;
    private static final int RECT_OVERFLOW = 16;
    private static final int RECT_UNDERFLOW = 6;
    private static final int SCALE_DOWN_IMAGE = 5;
    private static final int BASE_BLACK = 50;
    private static final ArrayList<Edge> EDGES = new ArrayList<>();
    private static final ArrayList<Rectangle> RECTANGLES = new ArrayList<>();
    private static final ArrayList<Vertice> VERTICES = new ArrayList<>();
    private static final ArrayList<ArrayList<Integer>> ADJ = new ArrayList<>(); // graph adjacency matrix

    public static void main(String[] args) {
        GrayscalePicture mainPicture = new GrayscalePicture("images/4.jpg");
        GrayscalePicture picture = createNewResizedImage(mainPicture, mainPicture.width(), mainPicture.height(), true);
        int width = picture.width();
        int height = picture.height();
        System.out.println("Picture width " + width + " height " + height);

        // start graph extracting process
        Edge firstEdge = firstBlackPixel(picture, width, height);
        if (firstEdge == null)
            return;
        System.out.println("First Edge " + firstEdge.toString());
        survey(picture, firstEdge.getY(), firstEdge.getX(), '-', null);
        deleteRepeatedEdges(EDGES); // edges get updated
        System.out.println("\nFound " + EDGES.size() + " edges");
        System.out.println("Edges are");
        for (Edge edge : EDGES)
            System.out.println(edge);
        ArrayList<Pixel> percentageEdges = getPercentage(EDGES, width, height);
        System.out.println("\nPercentage Measurement of Edges");
        System.out.println(percentageEdges);
        System.out.println("\nFound " + RECTANGLES.size() + " rectangles");
        System.out.println("Rectangles are");
        System.out.println(RECTANGLES);

        setVertices();
        setTopLeft(width, height);
        System.out.println("\nVertices");
        for (Vertice vertice : VERTICES)
            System.out.println(vertice);

        // ADJ
        createGraph();
        System.out.println("\nAdjacent Graph");
        for(int i=0;i<ADJ.size();i++)
            System.out.print(VERTICES.get(i).getId() + "   " + ADJ.get(i) + "\n");

        // Dijkstra
        System.out.println("\nDijkstra");

        // Paths
        System.out.println("\nPaths that got selected");
        for(Path path : findPaths()) {
            System.out.println("\n");
            System.out.println(path);
        }
    }//end main

    // usage
    public static DatasetEndpoint process(String fileDirecotory) {
        GrayscalePicture mainPicture = new GrayscalePicture(fileDirecotory);
        GrayscalePicture picture = createNewResizedImage(mainPicture, mainPicture.width(), mainPicture.height(), true);
        int width = picture.width();
        int height = picture.height();
        // start graph extracting process
        Edge firstEdge = firstBlackPixel(picture, width, height);
        Pixel firstPixel = new Pixel(firstEdge.getX(), firstEdge.getY());
        if (firstEdge == null)
            return null;
        survey(picture, firstEdge.getY(), firstEdge.getX(), '-', null);
        deleteRepeatedEdges(EDGES); // edges get updated
        setVertices();
        setTopLeft(width, height);
        // ADJ
        createGraph();
        ArrayList<Path> paths = findPaths();
        return new DatasetEndpoint(EDGES.size(), RECTANGLES.size(), firstPixel, VERTICES, paths);
    }//end process


    // find paths on graph
    private static ArrayList<Path> findPaths() {
        Dijkstra dijkstra = new Dijkstra();
        int switchesSize = RECTANGLES.size() - 1;
        ArrayList<Path> paths = new ArrayList<>();

        for(int i=0;i<switchesSize;i++) {
            Path path = null;
            ArrayList<Integer> pathIndexes = new ArrayList<>();
            // 1. between switches longest one gets selected
            for(int j=0;j<VERTICES.size();j++) {
                DijkstraData dijkstraData = new DijkstraData(j);
                // 2. closest switch to electricity gets selected
                if(VERTICES.get(j).getType().equals("switch")) {
                    // get switch paths to electricity
                    dijkstraData = dijkstra.dijkstra(ADJ, j);// now we have all paths
                    // choose closest to electricity
                    Path switchShortPath = getShortestPath(dijkstraData);

                    // 3. compare already existing switch path with new switch path
                    if(path == null)
                        path = switchShortPath;
                    else
                        path = getLongerPath(path, switchShortPath);
                }
            }//end for

            // 4. update vertices. now some boxes have electricity
            if(path == null)
                throw new Error("Something went wrong. Path should not be null");
            updateVertices(path.pathIndexes);
            paths.add(path);
        }//end for

        return paths;
    }//end findPaths

    private static void updateVertices(ArrayList<Integer> indexes) {
        for(int index : indexes) {
            VERTICES.get(index).setHasElectricity(true);
        }//end for
    }//end updateVertices

    private static Path getShortestPath(DijkstraData dijkstraData) {
        Path switchShortPath = null;
        int start = dijkstraData.getStart();
        for(int i=0;i<dijkstraData.destinations.size();i++) {
            int destination = dijkstraData.destinations.get(i);
            int distance = dijkstraData.distances.get(i);
            if(VERTICES.get(destination).hasElectricity() && !VERTICES.get(destination).getType().equals("switch")) {
                if(switchShortPath == null)
                    switchShortPath = new Path(start, destination, distance, VERTICES, dijkstraData.pathToDestination.get(i));
                else if(dijkstraData.distances.get(i) < switchShortPath.getDistance())
                    switchShortPath.changePath(dijkstraData.getStart(), dijkstraData.destinations.get(i), dijkstraData.distances.get(i), VERTICES, dijkstraData.pathToDestination.get(i));
            }
        }//end for
        return switchShortPath;
    }//end getShortestPath

    private static Path getLongerPath(Path first, Path second) {
        if(first.getDistance() >= second.getDistance())
            return first;
        else
            return second;
    }//end compareWithBestPath
    //end find paths on graph

    // direction evals == [u, r, l, d]
    private static void survey(GrayscalePicture picture, int row, int col, char direction, Vertice parentVertice) {
        //utilities
        Pixel nextPixel = direction == '-' ? null : nextPixelCalculation(col, row, direction, picture.width(), picture.height());
        //System.out.println(row + ", " + col + " " + direction);
        char[] paths = findPaths(picture, row, col);
        ArrayList<Character> newPaths = new ArrayList<>();
        // some utility conditions
        boolean existsAsRectangle = existsAsRectangle(col, row);
        boolean existsAsEdge = existsAsEdge(col, row);
        // end
        for (char path : paths) {
            if (path == direction || path == getContrastDirection(direction))
                continue;

            Pixel pixel = new Pixel(col, row);
            if (getBlackPixelsWidthInDirection(picture, pixel, path) > WALL_SIZE) {
                // path or rectangle

                Rectangle curRect;
                if (!existsAsRectangle && (curRect = isRectangle(picture, col, row, path)) != null) {
                    curRect.setParentVertice(parentVertice);
                    RECTANGLES.add(curRect);
                    // rectangle is assumed as vertice for graph vertices
                    String verticeID = curRect.getCenterX() + "," + curRect.getCenterY();
                    // new Vertice
                    parentVertice = new Vertice(curRect.getCenterX(), curRect.getCenterY(), verticeID, "switch", false);
                }
                else if (!existsAsEdge) {
                    if (!existsAsRectangle) {
                        Edge curEdge = new Edge(col, row, path, parentVertice);
                        EDGES.add(curEdge);
                        String verticeID = curEdge.getX() + "," + curEdge.getY();
                        parentVertice = new Vertice(curEdge.getX(), curEdge.getY(), verticeID, "distribution", false); // curEdge becomes the parent of the next paths that will get surveyed
                        newPaths.add(path); // new path that will get surveyed
                    }
                    else if (/* existsAsRectangle && */ nextPixel == null || picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) >= BASE_BLACK)
                        newPaths.add(path); // new path that will get surveyed
                }
            }
        }//end forEach paths

        for (char path : newPaths) { // survey for new paths (directions)
            survey(picture, row, col, path, parentVertice);
        }

        if (direction == '-')
            return;

        if (/*direction != '-' && */ (nextPixel == null || picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) >= BASE_BLACK)) {
            if (!existsAsEdge && !existsAsRectangle)
                EDGES.add(new Edge(col, row, direction, parentVertice)); // don't need to update existsAsEdge because this is the last command
        }
        else {
            //Pixel nextAppropriatePixel = continueStraightPath(picture, col, row, direction);
            survey(picture, nextPixel.getY(), nextPixel.getX(), direction, parentVertice);
        }
    }//end pathCycle

    private static Pixel continueStraightPath(GrayscalePicture picture, int col, int row, char direction) {
        // utilities
        char directionContrast = getContrastDirection(direction);
        Pixel surveyor = new Pixel(col, row);
        while (true) {
            Pixel nextPixel = nextPixelCalculation(surveyor.getX(), surveyor.getY(), direction, picture.width(), picture.height());
            if (nextPixel == null || picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) >= BASE_BLACK) // white space || out of bound
                return surveyor;
            ArrayList<Character> sides = new ArrayList<>(Arrays.asList('u', 'r', 'd', 'l'));
            sides.remove(Character.valueOf(direction));
            sides.remove(Character.valueOf(directionContrast));
            char[] paths = findPaths(picture, row, col);
            for (char path : paths) {
                if (path == direction || path == directionContrast)
                    continue;

                int pathLength = getBlackPixelsWidthInDirection(picture, surveyor, path);
                if (pathLength > WALL_SIZE)
                    if (!existsAsEdge(surveyor.getX(), surveyor.getY()))
                        return surveyor;
            }//end for

            // continue path
            surveyor.setX(nextPixel.getX());
            surveyor.setY(nextPixel.getY());
        }//end while
    }//end continueStraightPath

    private static Pixel nextPixelCalculation(int col, int row, char direction, int colMax, int rowMax) {
        if (direction == 'u')
            return (row - 1 < 0) ? null : new Pixel(col, row - 1);
        else if (direction == 'r')
            return (col + 1 >= colMax) ? null : new Pixel(col + 1, row);
        else if (direction == 'd')
            return (row + 1 >= rowMax) ? null : new Pixel(col, row + 1);
        else if (direction == 'l')
            return (col - 1 < 0) ? null : new Pixel(col - 1, row);
        else
            throw new Error("Direction " + direction + " is not valid.");
    }//end nextEdgeCalculation

    private static char[] findPaths(GrayscalePicture picture, int row, int col) {
        String pathsString = "";
        if ((col + 1 < picture.width() && col + 1 >= 0 && row < picture.height() && row >= 0) && picture.getGrayscale(col + 1, row) < BASE_BLACK) // right side
            pathsString += "r";
        if ((col - 1 < picture.width() && col - 1 >= 0 && row < picture.height() && row >= 0) && picture.getGrayscale(col - 1, row) < BASE_BLACK) // left side
            pathsString += "l";
        if ((col < picture.width() && col >= 0 && row - 1 < picture.height() && row - 1 >= 0) && picture.getGrayscale(col, row - 1) < BASE_BLACK) // up side
            pathsString += "u";
        if ((col < picture.width() && col >= 0 && row + 1 < picture.height() && row + 1 >= 0) && picture.getGrayscale(col, row + 1) < BASE_BLACK) // down side
            pathsString += "d";
        return pathsString.toCharArray();
    }//end findPaths

    private static Edge firstBlackPixel(GrayscalePicture picture, int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int curPixelValue = picture.getGrayscale(j, i);
                if (curPixelValue < 10)
                    return new Edge(j, i);
            }
        }
        return null;
    }//end firstBlackPixel

    private static char getContrastDirection(char direction) {
        if (direction == 'r')
            return 'l';
        else if (direction == 'l')
            return 'r';
        else if (direction == 'u')
            return 'd';
        else if (direction == 'd')
            return 'u';
        else
            return '_';
    }//end getContrastDirection

    private static int getBlackPixelsWidthInDirection(GrayscalePicture picture, Pixel pixel, char direction) {
        int length = 0;
        while (pixel.getX() >= 0 && pixel.getY() >= 0 && pixel.getX() < picture.width() && pixel.getY() < picture.height() && picture.getGrayscale(pixel.getX(), pixel.getY()) < BASE_BLACK) {
            if (direction == 'r')
                pixel.setX(pixel.getX() + 1);
            else if (direction == 'l')
                pixel.setX(pixel.getX() - 1);
            else if (direction == 'u')
                pixel.setY(pixel.getY() - 1);
            else if (direction == 'd')
                pixel.setY(pixel.getY() + 1);
            length++;
        }//end while
        return length;
    }//end movePixel

    private static GrayscalePicture createNewResizedImage(GrayscalePicture mainPicture, int mainWidth, int mainHeight, boolean saveToFile) {
        String matrixString = "";
        int width = mainPicture.width() / SCALE_DOWN_IMAGE + 1;
        int height = mainPicture.height() / SCALE_DOWN_IMAGE + 1;
        GrayscalePicture scaledPicture = new GrayscalePicture(width, height);

        int iScaled = 0;
        for (int i = 0; i < mainHeight; i += SCALE_DOWN_IMAGE) {
            int jScaled = 0;
            for (int j = 0; j < mainWidth; j += SCALE_DOWN_IMAGE) {
                int gray = mainPicture.getGrayscale(j, i);
                scaledPicture.setGrayscale(jScaled, iScaled, gray);
                String curPixel = String.valueOf(gray);
                if (curPixel.length() == 1)
                    curPixel = "  " + curPixel + "  ";
                else if (curPixel.length() == 2)
                    curPixel = "  " + curPixel + " ";
                else if (curPixel.length() == 3)
                    curPixel = " " + curPixel + " ";
                matrixString += curPixel;
                jScaled++;
            }
            matrixString += "\n";
            iScaled++;
        }

        if (saveToFile)
            try {
                File imageMatrixFile = new File("data/image-matrix.txt");
                FileWriter writer = new FileWriter(imageMatrixFile, false);
                writer.write(matrixString);
                writer.close();
            } catch (Exception e) {
                System.out.println("An Error Accrued " + e.getMessage());
            }

        return scaledPicture;
    }//end printDataToFile

    private static Rectangle isRectangle(GrayscalePicture picture, int startX, int startY, char direction) {
        // if null is returned this is not an rectangle
        // else is a rectangle ==> Rectangle is returned
        Pixel surveyor = new Pixel(startX, startY);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(1); // first pixel is black ==> initial length == 1
        rectangle.setHeight(1);
        boolean switchRectangleWidthHeight = false;
        if (direction == 'u' || direction == 'd') switchRectangleWidthHeight = true;
        while (true) {
            Pixel nextPixel = nextPixelCalculation(surveyor.getX(), surveyor.getY(), direction, picture.width(), picture.height());
            if (nextPixel == null) break; // out of bound image pixel. continue with other side
            if (picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) < BASE_BLACK) {
                rectangle.setWidth(rectangle.getWidth() + 1);
                if (rectangle.getWidth() > RECT_OVERFLOW)
                    return null;
                surveyor.setX(nextPixel.getX());
                surveyor.setY(nextPixel.getY());
            }
            else
                break; // reached end of black pixels. continue with other side
        }//end while
        // underflow ==> this is not a rectangle
        if (rectangle.getWidth() < RECT_UNDERFLOW)
            return null;

        ArrayList<Character> sideDirections = new ArrayList<>(Arrays.asList('l', 'r', 'u', 'd'));
        sideDirections.remove(Character.valueOf(direction));
        sideDirections.remove(Character.valueOf(getContrastDirection(direction)));

        Pixel edgePixel = new Pixel(surveyor.getX(), surveyor.getY());
        char[] edgePositionContrast = {getContrastDirection(direction), getContrastDirection(sideDirections.get(0))};
        for (int i = 0; i < sideDirections.size(); i++) {
            Pixel nextPixel = new Pixel(surveyor.getX(), surveyor.getY()); // initial value is current pixel at the rectangle side
            while (true) {
                nextPixel = nextPixelCalculation(nextPixel.getX(), nextPixel.getY(), sideDirections.get(i), picture.width(), picture.height());
                if (nextPixel == null) break; // out of bound image pixels
                if (picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) < BASE_BLACK) {
                    rectangle.setHeight(rectangle.getHeight() + 1);
                    if (rectangle.getHeight() > RECT_OVERFLOW)
                        return null;
                    if (i == 0) {
                        edgePixel.setY(nextPixel.getY());
                        edgePixel.setX(nextPixel.getX());
                    }
                }
                else
                    break; // reached end of black pixels
            }//end while
        }//end for
        // underflow ==> this is not a rectangle
        if (rectangle.getHeight() < RECT_UNDERFLOW)
            return null;

        //finding centerX && centerY
        if (switchRectangleWidthHeight) {
            int temp = rectangle.getWidth();
            rectangle.setWidth(rectangle.getHeight());
            rectangle.setHeight(temp);
        }
        int xRadius = (int) Math.ceil(rectangle.getWidth() / 2.0);
        int yRadius = (int) Math.ceil(rectangle.getHeight() / 2.0);
        if (edgePositionContrast[0] == 'l')
            xRadius *= -1;
        else if (edgePositionContrast[0] == 'u')
            yRadius *= -1;
        if (edgePositionContrast[1] == 'l')
            xRadius *= -1;
        else if (edgePositionContrast[1] == 'u')
            yRadius *= -1;
        rectangle.setCenterX(edgePixel.getX() + xRadius);
        rectangle.setCenterY(edgePixel.getY() + yRadius);
        //end finding centerX && centerY

        return rectangle;
    }//end isRectangle

    private static boolean existsAsRectangle(int x, int y) {
        for (Rectangle rectangle : RECTANGLES)
            if (rectangle.isInsideRectangle(x, y))
                return true;
        return false;
    }//end existsAsRectangle

    private static boolean existsAsEdge(int x, int y) {
        for (Edge edge : EDGES)
            if (edge.isInsideEdge(x, y, WALL_SIZE + 2))
                return true;
        return false;
    }//end existsAsEdge

    private static void deleteRepeatedEdges(ArrayList<Edge> edges) {
        ArrayList<Edge> result = new ArrayList<>();
        for (Edge edge : edges) {
            boolean exists = false;
            for (Edge item : result) {
                if (edge.getX() == item.getX() && edge.getY() == item.getY()) {
                    exists = true;
                    break;
                }
            }//end inner for
            if (!exists)
                result.add(edge);
        }//end outer for

        edges.clear();
        edges.addAll(result);
    }//end deleteRepeatedEdges

    private static ArrayList<Pixel> getPercentage(ArrayList<Edge> edges, int fullWidth, int fullHeight) {
        ArrayList<Pixel> percentageEdges = new ArrayList<>();
        for (Edge edge : edges) {
            int x = (int) (((float) edge.getX() / fullWidth) * 100);
            int y = (int) (((float) edge.getY() / fullHeight) * 100);
            percentageEdges.add(new Pixel(x, y));
        }//end for
        return percentageEdges;
    }//end getPercentage

    private static void setTopLeft(/* VERTICES */int fullWidth, int fullHeight) {
        for (Vertice vertice : VERTICES) {
            int x = (int) (((float) vertice.getX() / fullWidth) * 100);
            int y = (int) (((float) vertice.getY() / fullHeight) * 100);
            vertice.setTopLeft(y, x);
        }//end for
    }//end getPercentage

    private static void setVertices() {
        for (Edge edge : EDGES) {
            String verticeID = edge.getX() + "," + edge.getY();
            Vertice vertice = new Vertice(edge.getX(), edge.getY(), verticeID, "distribution", false);
            VERTICES.add(vertice);
        }//end for

        // find source box
        int source = 0;
        int area = RECTANGLES.get(0).getWidth() * RECTANGLES.get(0).getHeight();
        for (int i = 1; i < RECTANGLES.size(); i++) {
            int curArea = RECTANGLES.get(i).getWidth() * RECTANGLES.get(i).getHeight();
            if (curArea > area) {
                area = curArea;
                source = i;
            }
        }//end for
        // end find source box

        for (int i = 0; i < RECTANGLES.size(); i++) {
            String verticeID = RECTANGLES.get(i).getCenterX() + "," + RECTANGLES.get(i).getCenterY();
            Vertice vertice = new Vertice(RECTANGLES.get(i).getCenterX(), RECTANGLES.get(i).getCenterY(), verticeID, (i == source ? "source" : "switch"), (i == source));
            VERTICES.add(vertice);
        }//end for
    }//end setVertices


    // graph creation
    private static void createGraph() {
        // VERTICES, EDGES, RECTANGLES
        for (Vertice vertice : VERTICES) {
            vertice.adj.addAll(getAdjacentVertices(vertice.getX(), vertice.getY()));
        }//end for
        // now that vertices are full of adjacent vertices
        // fill adj array[][]

        // initialize ADJ
        for(int i=0;i<VERTICES.size();i++) {
            ADJ.add(new ArrayList<>());
            for(int j=0;j<VERTICES.size();j++) {
                ADJ.get(i).add(0);
            }
        }

        // set ADJ real values
        for(int i=0;i<VERTICES.size();i++) {
            for(int j=0;j<VERTICES.get(i).adj.size();j++) {
                int adjVerticeIndex = getVerticesIndex(VERTICES.get(i).adj.get(j).getX(), VERTICES.get(i).adj.get(j).getY());
                int xDistance = Math.abs(VERTICES.get(i).adj.get(j).getX() - VERTICES.get(i).getX());
                int yDistance = Math.abs(VERTICES.get(i).adj.get(j).getY() - VERTICES.get(i).getY());
                ADJ.get(i).set(adjVerticeIndex, Math.max(xDistance, yDistance));
            }
        }//end for
    }//end createGraph

    private static ArrayList<Vertice> getAdjacentVertices(int x, int y) {
        ArrayList<Vertice> adjs = new ArrayList<>();
        for (Edge edge : EDGES) {
            if (edge.getX() == x && edge.getY() == y) {
                if (edge.getParentVertice() != null)
                    adjs.add(edge.getParentVertice());
            }
            else if (edge.getParentVertice() != null && edge.getParentVertice().getX() == x && edge.getParentVertice().getY() == y) {
                adjs.add(getVertice(edge.getX(), edge.getY()));
            }
        }//end for

        for (Rectangle rectangle : RECTANGLES) {
            if(rectangle.getCenterX() == x && rectangle.getCenterY() == y) {
                if(rectangle.getParentVertice() != null)
                    adjs.add(rectangle.getParentVertice());
            }
            else if(rectangle.getParentVertice() != null && rectangle.getParentVertice().getX() == x && rectangle.getParentVertice().getY() == y) {
                adjs.add(getVertice(rectangle.getCenterX(), rectangle.getCenterY()));
            }
        }//end for
        return adjs;
    }//end getAdjacentVertices

    private static Vertice getVertice(int x, int y) {
        for (Vertice vertice : VERTICES) {
            if (vertice.getX() == x && vertice.getY() == y)
                return vertice;
        }
        return null;
    }//end getVertice

    private static int getVerticesIndex(int x, int y) {
        for(int i=0;i<VERTICES.size();i++) {
            if(VERTICES.get(i).getX() == x && VERTICES.get(i).getY() == y)
                return i;
        }//end for
        return -1;
    }//end getVerticesIndex

    // won't use for now
    private static int getWallWidth(GrayscalePicture picture) {
        HashMap<Integer, Integer> repeatedTimes = new HashMap<>();// repeated times for specific length of black pixels
        int length;
        for (int i = 0; i < picture.height(); i++) {
            length = 0;
            for (int j = 0; j < picture.width(); j++) {
                if (picture.getGrayscale(j, i) < 100) { // enter counting
                    length++;
                }
                else if (picture.getGrayscale(j, i) >= BASE_BLACK && length != 0) {
                    if (repeatedTimes.get(length) == null)
                        repeatedTimes.put(length, 1);
                    else {
                        int preValue = repeatedTimes.get(length);
                        repeatedTimes.put(length, preValue + 1);
                    }
                    length = 0;
                }
            }
        }//end for

        AtomicInteger mostRepeatedKey = new AtomicInteger(-1);
        AtomicInteger mostRepeatedLength = new AtomicInteger(-1000);
        repeatedTimes.remove(1);
        repeatedTimes.forEach((key, value) -> {
            if (value > mostRepeatedLength.get()) {
                mostRepeatedKey.set(key);
                mostRepeatedLength.set(value);
            }
        });

        return mostRepeatedKey.get();
    }//end getWallWidth
}//end class

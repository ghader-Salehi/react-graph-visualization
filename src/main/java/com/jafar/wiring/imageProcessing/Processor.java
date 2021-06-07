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
    private int WALL_SIZE = 5;
    private int RECT_OVERFLOW = 16;
    private int RECT_UNDERFLOW = 6;
    private int SCALE_DOWN_IMAGE = 5;
    private int BASE_BLACK = 50;
    private int DOOR_WIDTH = 50;
    private ArrayList<Edge> EDGES = new ArrayList<>();
    private ArrayList<Rectangle> RECTANGLES = new ArrayList<>();
    private ArrayList<Vertice> VERTICES = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> ADJ = new ArrayList<>(); // graph adjacency matrix

    private final String FILE_DIRECTORY;
    private final boolean DOOR_FLAG;


    /*
    public static void main(String[] args) {
        Processor processor = new Processor("images/11.jpg");
        GrayscalePicture mainPicture = new GrayscalePicture("images/5.jpg");
        GrayscalePicture picture = createNewResizedImage(mainPicture, mainPicture.width(), mainPicture.height(), false);
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
        pairDoors(picture);
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
        deleteEdgesInsideSource();
        System.out.println("\nVertices");
        for (Vertice vertice : VERTICES)
            System.out.println(vertice);

        // ADJ
        createGraph();
        System.out.println("\nAdjacent Graph");
        for (int i = 0; i < ADJ.size(); i++)
            System.out.print(VERTICES.get(i).getId() + "   " + ADJ.get(i) + "\n");

        // Dijkstra
        System.out.println("\nDijkstra");

        // Paths
        System.out.println("\nPaths that got selected");
        for (Path path : findPaths()) {
            System.out.println("\n");
            System.out.println(path);
        }
    }//end main
    */


    // usage
    public Processor(String FILE_DIRECTORY, boolean DOOR_FLAG) {
        this.FILE_DIRECTORY = FILE_DIRECTORY;
        this.DOOR_FLAG = DOOR_FLAG;
    }

    public DatasetEndpoint process(boolean fileMinimized) {
        GrayscalePicture picture;
        if(fileMinimized)
            picture = new GrayscalePicture(FILE_DIRECTORY);
        else {
            GrayscalePicture mainPicture = new GrayscalePicture(FILE_DIRECTORY);
            picture = createNewResizedImage(mainPicture, mainPicture.width(), mainPicture.height(), false);
            File file = new File(FILE_DIRECTORY);
            if(file.exists())
                file.delete();
            picture.save(FILE_DIRECTORY);
        }
        int width = picture.width();
        int height = picture.height();
        // start graph extracting process
        Edge firstEdge = firstBlackPixel(picture, width, height);
        if (firstEdge == null) return null;
        Pixel firstPixel = new Pixel(firstEdge.getX(), firstEdge.getY());
        survey(picture, firstEdge.getY(), firstEdge.getX(), '-', null);
        deleteRepeatedEdges(EDGES); // edges get updated
        pairDoors(picture);
        setVertices();
        setTopLeft(width, height);
        // ADJ
        createGraph();
        ArrayList<Path> paths = findPaths();
        if(DOOR_FLAG)
            removeDoorEdges();

        return new DatasetEndpoint(EDGES.size(), RECTANGLES.size(), firstPixel, VERTICES, paths);
    }//end process


    // find paths on graph
    private ArrayList<Path> findPaths() {
        Dijkstra dijkstra = new Dijkstra();
        int switchesSize = RECTANGLES.size() - 1;
        ArrayList<Path> paths = new ArrayList<>();

        for (int i = 0; i < switchesSize; i++) {
            Path path = null;
            // 1. between switches longest one gets selected
            for (int j = 0; j < VERTICES.size(); j++) {
                DijkstraData dijkstraData = new DijkstraData(j);
                // 2. closest switch to electricity gets selected
                if (VERTICES.get(j).getType().equals("switch") && !VERTICES.get(j).hasElectricity()) {
                    // get switch paths to electricity
                    dijkstraData = dijkstra.dijkstra(ADJ, j);// now we have all paths
                    // choose closest to electricity
                    Path switchShortPath = getShortestPath(dijkstraData);

                    // 3. compare already existing switch path with new switch path
                    if (path == null)
                        path = switchShortPath;
                    else
                        path = getLongerPath(path, switchShortPath);
                }
            }//end for

            // 4. update vertices. now some boxes have electricity
            if (path == null)
                continue;
//                throw new Error("Something went wrong. Path should not be null");
            updateVertices(path.pathIndexes);
            paths.add(path);
        }//end for

        return paths;
    }//end findPaths

    private void updateVertices(ArrayList<Integer> indexes) {
        for (int index : indexes) {
            VERTICES.get(index).setHasElectricity(true);
        }//end for
    }//end updateVertices

    private Path getShortestPath(DijkstraData dijkstraData) {
        Path switchShortPath = null;
        int start = dijkstraData.getStart();
        for (int i = 0; i < dijkstraData.destinations.size(); i++) {
            int destination = dijkstraData.destinations.get(i);
            int distance = dijkstraData.distances.get(i);
            if (VERTICES.get(destination).hasElectricity() && !VERTICES.get(destination).getType().equals("switch")) {
                if (switchShortPath == null)
                    switchShortPath = new Path(start, destination, distance, VERTICES, dijkstraData.pathToDestination.get(i));
                else if (dijkstraData.distances.get(i) < switchShortPath.getDistance())
                    switchShortPath.changePath(dijkstraData.getStart(), dijkstraData.destinations.get(i), dijkstraData.distances.get(i), VERTICES, dijkstraData.pathToDestination.get(i));
            }
        }//end for
        return switchShortPath;
    }//end getShortestPath

    private Path getLongerPath(Path first, Path second) {
        if (first.getDistance() >= second.getDistance())
            return first;
        else
            return second;
    }//end compareWithBestPath

    private void removeDoorEdges() {
        ArrayList<Edge> removedEdges = new ArrayList<>();
        for(Edge edge : EDGES) {
            if(edge.isDoor()) {
                int verticeIndex = getVerticesIndex(edge.getX(), edge.getY());
                if(verticeIndex != -1) {
                    VERTICES.remove(verticeIndex);
                }
                removedEdges.add(edge);
            }
        }

        for(Edge edge : removedEdges)
            EDGES.remove(edge);
    }//end removeDoorEdges

    //end find paths on graph

    // direction evals == [u, r, l, d]
    private void survey(GrayscalePicture picture, int row, int col, char direction, Vertice parentVertice) {
        //utilities
        Pixel nextPixel = direction == '-' ? null : nextPixelCalculation(picture, col, row, direction, picture.width(), picture.height());
        char contrastDirection = getContrastDirection(direction);
        //System.out.println(row + ", " + col + " " + direction);
        char[] paths = findPaths(picture, row, col);
        ArrayList<Character> newPaths = new ArrayList<>();
        // some utility conditions
        boolean existsAsRectangle = existsAsRectangle(col, row);
        boolean existsAsEdge = existsAsEdge(col, row);
        // end
        for (char path : paths) {
            if (path == direction || path == contrastDirection)
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
                        existsAsEdge = true;
                        Edge curEdge = new Edge(col, row, path, parentVertice);
                        curEdge.addDirection(direction);
                        curEdge.addDirection(contrastDirection);
                        EDGES.add(curEdge);
                        String verticeID = curEdge.getX() + "," + curEdge.getY();
                        parentVertice = new Vertice(curEdge.getX(), curEdge.getY(), verticeID, "distribution", false); // curEdge becomes the parent of the next paths that will get surveyed
                        newPaths.add(path); // new path that will get surveyed
                    }
                    else if (/* existsAsRectangle && */ nextPixel == null || picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) >= BASE_BLACK)
                        newPaths.add(path); // new path that will get surveyed
                }
                else if (/* existsAsEdge && */ !existsAsEdgeInDirection(col, row, path)) {
                    addDirectionToExistingEdge(col, row, path);
                    newPaths.add(path);
                }
            }
        }//end forEach paths

        for (char path : newPaths) { // survey for new paths (directions)
            survey(picture, row, col, path, parentVertice);
        }

        if (direction == '-')
            return;

        if (/*direction != '-' && */ (nextPixel == null || picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) >= BASE_BLACK)) {
            if (!existsAsEdge && !existsAsRectangle) {
                Edge edge = new Edge(col, row, parentVertice, true);
                edge.addDirection(contrastDirection);
                EDGES.add(edge); // don't need to update existsAsEdge because this is the last command
            }
        }
        else {
            //Pixel nextAppropriatePixel = continueStraightPath(picture, col, row, direction);
            survey(picture, nextPixel.getY(), nextPixel.getX(), direction, parentVertice);
        }
    }//end pathCycle

    private void pairDoors(GrayscalePicture picture) {
        ArrayList<Edge> notPairedEdges = new ArrayList<>();
        ArrayList<Character> notPairedDirections = new ArrayList<>();
        // EDGES
        for (Edge edge : EDGES) {
            if (edge.isDoor() && !edge.isPaired()) {
                char pairDirection = getContrastDirection(edge.getDirections().get(0));
                boolean paired = false;
                for (Edge pairEdge : EDGES) {
                    if (pairEdge.isDoor() && !pairEdge.isPaired() && pairEdge.containsDirection(pairDirection)) {
                        int verticalDistance = Math.abs(edge.getY() - pairEdge.getY());
                        int horizontalDistance = Math.abs(edge.getX() - pairEdge.getX());
                        if (pairDirection == 'u' || pairDirection == 'd') {
                            if (verticalDistance < DOOR_WIDTH && verticalDistance > WALL_SIZE && horizontalDistance < WALL_SIZE) {
                                edge.setPaired(true);
                                pairEdge.setPaired(true);
                                paired = true;
                                break;
                            }
                        }
                        else { // pairDirection == 'l' || 'r'
                            if (horizontalDistance < DOOR_WIDTH && horizontalDistance > WALL_SIZE && verticalDistance < WALL_SIZE) {
                                edge.setPaired(true);
                                pairEdge.setPaired(true);
                                paired = true;
                                break;
                            }
                        }
                    }
                }//end for pairEdge
                if (!paired) {
                    notPairedEdges.add(edge);
                    notPairedDirections.add(pairDirection);
                }
            }
        }//end for

        for (int i = 0; i < notPairedEdges.size(); i++)
            findPair(notPairedEdges.get(i), notPairedDirections.get(i), picture);

    }//end pairDoors

    private void findPair(Edge edge, char direction, GrayscalePicture picture) {
        int colMax = picture.width();
        int rowMax = picture.height();
        Pixel surveyor = nextPixelCalculation(edge.getX(), edge.getY(), direction, colMax, rowMax);
        while (surveyor != null && picture.getGrayscale(surveyor.getX(), surveyor.getY()) >= BASE_BLACK && isThick(picture, surveyor.getX(), surveyor.getY()))
            surveyor = nextPixelCalculation(surveyor.getX(), surveyor.getY(), direction, colMax, rowMax);
        if (surveyor == null)
            throw new Error("Surveyor can not be null.");
        Edge pairEdge = new Edge(surveyor.getX(), surveyor.getY(), null, direction, true);
        if(!existsAsEdge(pairEdge.getX(), pairEdge.getY())) {
            EDGES.add(pairEdge);
            survey(picture, surveyor.getY(), surveyor.getX(), direction, null);
        }
    }//end findPair

    private boolean isThick(GrayscalePicture picture, int col, int row) {
        int horizontal = 1;
        int vertical = 1;
        int requiredSize = WALL_SIZE / 2;
        int colMax = picture.width();
        int rowMax = picture.height();
        char[] directions = {'u', 'd', 'r', 'l'};
        for (char direction : directions) {
            if (direction == 'u' || direction == 'd') {
                Pixel nextPixel = nextPixelCalculation(col, row, direction, colMax, rowMax);
                while (vertical < requiredSize && nextPixel != null && picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) < BASE_BLACK) {
                    vertical++;
                    nextPixel = nextPixelCalculation(nextPixel.getX(), nextPixel.getY(), direction, colMax, rowMax);
                }
            }
            else { // direction == r || direction == l
                Pixel nextPixel = nextPixelCalculation(col, row, direction, colMax, rowMax);
                while (horizontal < requiredSize && nextPixel != null && picture.getGrayscale(nextPixel.getX(), nextPixel.getY()) < BASE_BLACK) {
                    horizontal++;
                    nextPixel = nextPixelCalculation(nextPixel.getX(), nextPixel.getY(), direction, colMax, rowMax);
                }
            }
        }//end for
        return !(horizontal >= requiredSize && vertical >= requiredSize);
    }//end isThick

    private Pixel continueStraightPath(GrayscalePicture picture, int col, int row, char direction) {
        // utilities
        char directionContrast = getContrastDirection(direction);
        Pixel surveyor = new Pixel(col, row);
        while (true) {
            Pixel nextPixel = nextPixelCalculation(picture, surveyor.getX(), surveyor.getY(), direction, picture.width(), picture.height());
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

    private Pixel nextPixelCalculation(GrayscalePicture picture, int col, int row, char direction, int colMax, int rowMax) {
        Pixel nextPixel;
        if (direction == 'u') {
            if (row - 1 < 0)
                return null;
            if (picture.getGrayscale(col, row - 1) >= BASE_BLACK) {
                if (col + 1 < colMax && picture.getGrayscale(col + 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col + 1, row - 1);
                else if (col - 1 >= 0 && picture.getGrayscale(col - 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col - 1, row - 1);
                else
                    return null;
            }
            else
                nextPixel = new Pixel(col, row - 1);
        }
        else if (direction == 'r') {
            if (col + 1 >= colMax)
                return null;
            if (picture.getGrayscale(col + 1, row) >= BASE_BLACK) {
                if (row + 1 < rowMax && picture.getGrayscale(col + 1, row + 1) < BASE_BLACK)
                    nextPixel = new Pixel(col + 1, row + 1);
                else if (row - 1 >= 0 && picture.getGrayscale(col + 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col + 1, row - 1);
                else
                    return null;
            }
            else
                nextPixel = new Pixel(col + 1, row);
        }
        else if (direction == 'd') {
            if (row + 1 >= rowMax)
                return null;
            if (picture.getGrayscale(col, row + 1) >= BASE_BLACK) {
                if (col + 1 < colMax && picture.getGrayscale(col + 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col + 1, row + 1);
                else if (col - 1 >= 0 && picture.getGrayscale(col - 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col - 1, row + 1);
                else
                    return null;
            }
            else
                nextPixel = new Pixel(col, row + 1);
        }
        else if (direction == 'l') {
            if (col - 1 < 0)
                return null;
            if (picture.getGrayscale(col - 1, row) >= BASE_BLACK) {
                if (row + 1 < rowMax && picture.getGrayscale(col - 1, row + 1) < BASE_BLACK)
                    nextPixel = new Pixel(col - 1, row + 1);
                else if (row - 1 >= 0 && picture.getGrayscale(col - 1, row - 1) < BASE_BLACK)
                    nextPixel = new Pixel(col - 1, row - 1);
                else
                    return null;
            }
            else
                nextPixel = new Pixel(col - 1, row);
        }
        else
            throw new Error("Direction " + direction + " is not valid.");

        return isThick(picture, nextPixel.getX(), nextPixel.getY()) ? null : nextPixel;
    }//end nextEdgeCalculation

    private Pixel nextPixelCalculation(int col, int row, char direction, int colMax, int rowMax) {
        if (direction == 'u')
            return (row - 1 < 0) ? null : new Pixel(col, row - 1);
        else if (direction == 'd')
            return (row + 1 >= rowMax) ? null : new Pixel(col, row + 1);
        else if (direction == 'r')
            return (col + 1 >= colMax) ? null : new Pixel(col + 1, row);
        else if (direction == 'l')
            return (col - 1 < 0) ? null : new Pixel(col - 1, row);
        else
            throw new Error("Direction " + direction + " is not valid.");
    }//end nextPixelCalculation

    private char[] findPaths(GrayscalePicture picture, int row, int col) {
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

    private Edge firstBlackPixel(GrayscalePicture picture, int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int curPixelValue = picture.getGrayscale(j, i);
                if (curPixelValue < 10 && !isThick(picture, j, i))
                    return new Edge(j, i);
            }
        }
        return null;
    }//end firstBlackPixel

    private char getContrastDirection(char direction) {
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

    private int getBlackPixelsWidthInDirection(GrayscalePicture picture, Pixel pixel, char direction) {
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

    private GrayscalePicture createNewResizedImage(GrayscalePicture mainPicture, int mainWidth, int mainHeight, boolean saveToFile) {
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

    private Rectangle isRectangle(GrayscalePicture picture, int startX, int startY, char direction) {
        // if null is returned this is not an rectangle
        // else is a rectangle ==> Rectangle is returned
        Pixel surveyor = new Pixel(startX, startY);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(1); // first pixel is black ==> initial length == 1
        rectangle.setHeight(1);
        boolean switchRectangleWidthHeight = false;
        if (direction == 'u' || direction == 'd') switchRectangleWidthHeight = true;
        while (true) {
            Pixel nextPixel = nextPixelCalculation(picture, surveyor.getX(), surveyor.getY(), direction, picture.width(), picture.height());
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
                nextPixel = nextPixelCalculation(picture, nextPixel.getX(), nextPixel.getY(), sideDirections.get(i), picture.width(), picture.height());
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

    private boolean existsAsRectangle(int x, int y) {
        for (Rectangle rectangle : RECTANGLES)
            if (rectangle.isInsideRectangle(x, y))
                return true;
        return false;
    }//end existsAsRectangle

    private boolean existsAsEdge(int x, int y) {
        for (Edge edge : EDGES)
            if (edge.isInsideEdge(x, y, WALL_SIZE + 2))
                return true;
        return false;
    }//end existsAsEdge

    private boolean existsAsEdgeInDirection(int x, int y, char direction) {
        for (Edge edge : EDGES)
            if (edge.isInsideEdge(x, y, WALL_SIZE + 2) && edge.containsDirection(direction))
                return true;
        return false;
    }//end existsAsEdgeInDirection

    private void addDirectionToExistingEdge(int col, int row, char direction) {
        for (Edge edge : EDGES)
            if (edge.isInsideEdge(col, row, WALL_SIZE + 2) && !edge.containsDirection(direction))
                edge.addDirection(direction);
    }//end addDirectionToExistingEdge

    private void deleteRepeatedEdges(ArrayList<Edge> edges) {
        ArrayList<Edge> result = new ArrayList<>();
        int counter = 0;
        for (Edge edge : edges) {
            boolean exists = false;
            for (Edge item : result) {
                if (edge.isInsideEdge(item.getX(), item.getY(), WALL_SIZE + 2)) {
                    exists = true;
                    break;
                }
            }//end inner for
            if (!exists)
                result.add(edge);
            else
                counter++;
        }//end outer for

        System.out.println("There was " + counter + " repeated items.");

        edges.clear();
        edges.addAll(result);
    }//end deleteRepeatedEdges

    private void deleteEdgesInsideSource() {

    }//end deleteEdgesInsideSource

    private ArrayList<Pixel> getPercentage(ArrayList<Edge> edges, int fullWidth, int fullHeight) {
        ArrayList<Pixel> percentageEdges = new ArrayList<>();
        for (Edge edge : edges) {
            int x = (int) (((float) edge.getX() / fullWidth) * 100);
            int y = (int) (((float) edge.getY() / fullHeight) * 100);
            percentageEdges.add(new Pixel(x, y));
        }//end for
        return percentageEdges;
    }//end getPercentage

    private void setTopLeft(/* VERTICES */int fullWidth, int fullHeight) {
        for (Vertice vertice : VERTICES) {
            int x = (int) (((float) vertice.getX() / fullWidth) * 100);
            int y = (int) (((float) vertice.getY() / fullHeight) * 100);
            vertice.setTopLeft(y, x);
        }//end for
    }//end getPercentage

    private void setVertices() {
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

        // remove edge inside of source (if there is any)
        Edge edgeInsideSource = null;
        for(int i=0;i<EDGES.size();i++) {
            if(RECTANGLES.get(source).isInsideRectangle(EDGES.get(i).getX(), EDGES.get(i).getY())) {
                edgeInsideSource = EDGES.remove(i);
                RECTANGLES.get(source).setParentVertice(edgeInsideSource.getParentVertice());
                break;
            }
        }

        // change parent of edges/rectangles where equaled to removed edge
        if(edgeInsideSource != null) {
            String verticeID = RECTANGLES.get(source).getCenterX() + "," + RECTANGLES.get(source).getCenterY();
            Vertice sourceVertice = new Vertice(RECTANGLES.get(source).getCenterX(), RECTANGLES.get(source).getCenterY(), verticeID, "source", true);
            for(Edge edge : EDGES) {
                if(edge.getParentVertice() == null) continue;
                if(edge.getParentVertice().getX() == edgeInsideSource.getX() && edge.getParentVertice().getY() == edgeInsideSource.getY()) {
                    edge.setParentVertice(sourceVertice);
                }
            }

            for(int i=0;i<RECTANGLES.size();i++) {
                if(RECTANGLES.get(i).getParentVertice() == null || i == source) continue;
                if(RECTANGLES.get(i).getParentVertice().getX() == edgeInsideSource.getX() && RECTANGLES.get(i).getParentVertice().getY() == edgeInsideSource.getY()) {
                    RECTANGLES.get(i).setParentVertice(sourceVertice);
                }
            }
        }

        for (Edge edge : EDGES) {
            String verticeID = edge.getX() + "," + edge.getY();
            Vertice vertice = new Vertice(edge.getX(), edge.getY(), verticeID, "distribution", false);
            VERTICES.add(vertice);
        }//end for

        for (int i = 0; i < RECTANGLES.size(); i++) {
            String verticeID = RECTANGLES.get(i).getCenterX() + "," + RECTANGLES.get(i).getCenterY();
            Vertice vertice = new Vertice(RECTANGLES.get(i).getCenterX(), RECTANGLES.get(i).getCenterY(), verticeID, (i == source ? "source" : "switch"), (i == source));
            VERTICES.add(vertice);
        }//end for
    }//end setVertices

    // graph creation
    private void createGraph() {
        // VERTICES, EDGES, RECTANGLES
        for (Vertice vertice : VERTICES) {
            vertice.adj.addAll(getAdjacentVertices(vertice.getX(), vertice.getY()));
        }//end for
        // now that vertices are full of adjacent vertices
        // fill adj array[][]

        // initialize ADJ
        for (int i = 0; i < VERTICES.size(); i++) {
            ADJ.add(new ArrayList<>());
            for (int j = 0; j < VERTICES.size(); j++) {
                ADJ.get(i).add(0);
            }
        }

        // set ADJ real values
        for (int i = 0; i < VERTICES.size(); i++) {
            for (int j = 0; j < VERTICES.get(i).adj.size(); j++) {
                int adjVerticeIndex = getVerticesIndex(VERTICES.get(i).adj.get(j).getX(), VERTICES.get(i).adj.get(j).getY());
                int xDistance = Math.abs(VERTICES.get(i).adj.get(j).getX() - VERTICES.get(i).getX());
                int yDistance = Math.abs(VERTICES.get(i).adj.get(j).getY() - VERTICES.get(i).getY());
                    ADJ.get(i).set(adjVerticeIndex, Math.max(xDistance, yDistance));
            }
        }//end for
    }//end createGraph

    private ArrayList<Vertice> getAdjacentVertices(int x, int y) {
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
            if (rectangle.getCenterX() == x && rectangle.getCenterY() == y) {
                if (rectangle.getParentVertice() != null)
                    adjs.add(rectangle.getParentVertice());
            }
            else if (rectangle.getParentVertice() != null && rectangle.getParentVertice().getX() == x && rectangle.getParentVertice().getY() == y) {
                adjs.add(getVertice(rectangle.getCenterX(), rectangle.getCenterY()));
            }
        }//end for
        return adjs;
    }//end getAdjacentVertices

    private Vertice getVertice(int x, int y) {
        for (Vertice vertice : VERTICES) {
            if (vertice.getX() == x && vertice.getY() == y)
                return vertice;
        }
        return null;
    }//end getVertice

    private int getVerticesIndex(int x, int y) {
        for (int i = 0; i < VERTICES.size(); i++) {
            if (VERTICES.get(i).getX() == x && VERTICES.get(i).getY() == y)
                return i;
        }//end for
        return -1;
    }//end getVerticesIndex

    // won't use for now
    private int getWallWidth(GrayscalePicture picture) {
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
package com.jafar.wiring.imageProcessing.graphDetection;

public class Rectangle {
    private int centerX;
    private int centerY;
    private int width;
    private int height;
    private Vertice parentVertice;
    private boolean source;


    public Rectangle() {
    }

    public Rectangle(int centerX, int centerY, int width, int height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
    }

    public Rectangle(int centerX, int centerY, int width, int height, Vertice parentVertice) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.parentVertice = parentVertice;
    }

    public Rectangle(int centerX, int centerY, int width, int height, Vertice parentVertice, boolean source) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.parentVertice = parentVertice;
        this.source = source;
    }

    public boolean isInsideRectangle(int pixelX, int pixelY) {
        int verticalRadius = (int) Math.ceil(height / 2.0) + 3;
        int horizontalRadius = (int) Math.ceil(width / 2.0) + 3;
        if((pixelX > centerX - horizontalRadius) && (pixelX < centerX + horizontalRadius) && (pixelY < centerY + verticalRadius) && (pixelY > centerY - verticalRadius))
            return true;
        return false;
    }


    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Vertice getParentVertice() {
        return parentVertice;
    }

    public void setParentVertice(Vertice parentVertice) {
        this.parentVertice = parentVertice;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", width=" + width +
                ", height=" + height +
                ", parent=" + parentVertice.getX() + "," + parentVertice.getY() +
                '}';
    }
}//end class

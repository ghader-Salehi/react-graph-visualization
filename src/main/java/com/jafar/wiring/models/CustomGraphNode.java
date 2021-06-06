package com.jafar.wiring.models;

public class CustomGraphNode {
    private String id;
    private String color;

    public CustomGraphNode() {
    }

    public CustomGraphNode(String id, String color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

package com.jafar.wiring.models;

public class CustomGraphLink {
    private String source;
    private String target;
    private String label;
    private String color;

    public CustomGraphLink(String source, String target, String label, String color) {
        this.source = source;
        this.target = target;
        this.label = label;
        this.color = color;
    }

    public CustomGraphLink() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

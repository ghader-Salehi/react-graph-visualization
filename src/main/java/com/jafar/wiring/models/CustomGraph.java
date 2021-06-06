package com.jafar.wiring.models;

public class CustomGraph {
    private CustomGraphNode[] nodes;
    private CustomGraphLink[] links;

    public CustomGraph() {
    }

    public CustomGraphNode[] getNodes() {
        return nodes;
    }

    public void setNodes(CustomGraphNode[] nodes) {
        this.nodes = nodes;
    }

    public CustomGraphLink[] getLinks() {
        return links;
    }

    public void setLinks(CustomGraphLink[] links) {
        this.links = links;
    }
}

package com.ferooz.sourcecodearchitect.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * Created by Ferooz on 12/01/17.
 */
public class Edge extends DefaultEdge{
    private Node v1;
    private Node v2;
    private String label;

    public Edge(Node v1, Node v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public Node getV1() {
        return v1;
    }

    public Node getV2() {
        return v2;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "v1=" + v1.startLine +
                ", v2=" + v2.startLine +
                ", label='" + label + '\'' +
                '}';
    }
}

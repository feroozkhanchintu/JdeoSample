package com.ferooz.sourcecodearchitect.handlers;

import com.ferooz.sourcecodearchitect.graph.Edge;
import com.ferooz.sourcecodearchitect.graph.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Created by Ferooz on 12/01/17.
 */
public class Handler {
    public static DirectedGraph<Node, Edge> graph;
    public static CompilationUnit compilationUnit;

    static {
        graph = new DefaultDirectedGraph<Node, Edge>(Edge.class);
    }


    public void handle(ASTNode preNode, ASTNode parent, Node nextNode){

    }
}

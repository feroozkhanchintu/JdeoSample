package com.ferooz.sourcecodearchitect.handlers;

import com.ferooz.sourcecodearchitect.graph.Node;
import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Created by swapnil on 13/01/17.
 */
public class ConvertToGraphNode {
    public static Node convertToGraphNode(ASTNode astNode){
        Node node = new Node("", "", Handler.compilationUnit.getLineNumber(astNode.getStartPosition()),
                astNode.getStartPosition(),astNode.getLength(),0);

        return node;
    }
}

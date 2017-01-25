package com.ferooz.sourcecodearchitect.handlers;

import com.ferooz.sourcecodearchitect.graph.Edge;
import com.ferooz.sourcecodearchitect.graph.Node;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * Created by Ferooz on 13/01/17.
 */
public class IfStatementHandler extends Handler {

//    private IfStatement ifStatement;
//
//    public IfStatementHandler(IfStatement ifStatement){
//        this.ifStatement = ifStatement;
//    }
//
//    @Override
//    public void handle(Node preNode, Node parent, Node nextNode) {
//
////        parent = nodeToAdd;
//        Statement ifStatementThenBlock = ifStatement.getThenStatement();
//
//        handleIfStatement(preNode, parent, nextNode, ifStatementThenBlock);
//
//        Statement ifStatementElseBlock = ifStatement.getElseStatement();
//
//        if (ifStatementElseBlock instanceof IfStatement) {
//            IfStatement ifStatement1 = (IfStatement) ifStatement.getElseStatement();
////            handleIfStatement(preNode, parent, nextNode, ifStatement1.getThenStatement());
//            new BlockHandler((Block) ifStatement1.getThenStatement());
//        } else {
//            if (!(ifStatementElseBlock instanceof EmptyStatement)) {
//                Block elseBlock = (Block) ifStatement.getElseStatement();
//                if (elseBlock != null) {
//                    if (elseBlock.statements().size() > 0) {
//                        Handler blockHandler = new BlockHandler(elseBlock);
//                        blockHandler.handle(preNode, parent, nextNode);
//
//                        Statement statement = (Statement) elseBlock.statements().get(elseBlock.statements().size() - 1);
//                        Node node = new Node("", "", compilationUnit.getLineNumber(statement.getStartPosition()),
//                                0, 0, 0);
//                        graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//                    } else {
//                        Node node = new Node("", "", compilationUnit.getLineNumber(ifStatement.getStartPosition()),
//                                0, 0, 0);
//                        graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//
//                    }
//                } else {
//
//                    Node node = new Node("", "", compilationUnit.getLineNumber(ifStatement.getStartPosition()),
//                            0, 0, 0);
//                    graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//
//                }
//            }
//        }
//    }
//
//    private void handleIfStatement(Node preNode, Node parent, Node nextNode, Statement ifStatementThenBlock) {
//        if (!(ifStatementThenBlock instanceof EmptyStatement)) {
//            Block thenBlock = (Block) ifStatementThenBlock;
//            if (thenBlock != null) {
//                if (thenBlock.statements().size() > 0) {
//                    Handler blockHandler = new BlockHandler(thenBlock);
//                    blockHandler.handle(preNode, parent, nextNode);
//
//                    Statement statement = (Statement) thenBlock.statements().get(thenBlock.statements().size() - 1);
//                    Node node = new Node("", "", compilationUnit.getLineNumber(statement.getStartPosition()),
//                            0, 0, 0);
//                    graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//                } else {
//
//                    Node node = new Node("", "", compilationUnit.getLineNumber(ifStatement.getStartPosition()),
//                            0, 0, 0);
//                    graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//
//                }
//            } else {
//
//                Node node = new Node("", "", compilationUnit.getLineNumber(ifStatement.getStartPosition()),
//                        0, 0, 0);
//                graph.addEdge(node, nextNode, new Edge(node, nextNode, ""));
//
//            }
//        }
//    }

}

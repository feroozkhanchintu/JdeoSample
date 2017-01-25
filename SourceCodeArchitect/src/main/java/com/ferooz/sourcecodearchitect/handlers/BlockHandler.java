package com.ferooz.sourcecodearchitect.handlers;

import com.ferooz.sourcecodearchitect.graph.Edge;
import com.ferooz.sourcecodearchitect.graph.Node;
import org.eclipse.jdt.core.dom.*;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ferooz on 12/01/17.
 */
public class BlockHandler extends Handler {

    private Block node;
    private static boolean entryNodeCreated = false;

    public BlockHandler(Block node) {
        this.node = node;
    }

    public void handle(ASTNode preNode, ASTNode parent, Node nextNode) {
        ListIterator<Statement> statementIterator = node.statements().listIterator();
        while (statementIterator.hasNext()) {

//            Statement preStatement = null;
//            if (statementIterator.hasPrevious()) {
//                preStatement = statementIterator.previous();
//                statementIterator.next();
//            }

            Statement statement = statementIterator.next();
            Node nextToCondition = nextNode;
            if (statement.getNodeType() == ASTNode.IF_STATEMENT && statementIterator.hasNext()) {
                Statement nextStatement = statementIterator.next();
                nextToCondition = ConvertToGraphNode.convertToGraphNode(nextStatement);
                graph.addVertex(nextToCondition);
                statementIterator.previous();
            }


            Node nodeToAdd = ConvertToGraphNode.convertToGraphNode(statement);

            if (!graph.containsVertex(nodeToAdd))
                graph.addVertex(nodeToAdd);

            if (!(preNode instanceof IfStatement) || parent instanceof IfStatement)
                graph.addEdge(ConvertToGraphNode.convertToGraphNode(preNode),
                        nodeToAdd, new Edge(ConvertToGraphNode.convertToGraphNode(preNode), nodeToAdd, ""));
            else {
                System.out.println("");
            }

            preNode = statement;


//            if (preStatement != null) {
//                if (preStatement.getNodeType() == ASTNode.IF_STATEMENT) {
//                    IfStatement ifStatement = (IfStatement) preStatement;
//                    if (ifStatement.getElseStatement() != null && !(ifStatement.getElseStatement() instanceof EmptyStatement)) {
//
//                        Block elseBlock;
//
//                        while (ifStatement == null || !(ifStatement.getElseStatement() instanceof Block)) {
//                            if (ifStatement.getElseStatement() != null)
//                                ifStatement = (IfStatement) ifStatement.getElseStatement();
//                            else
//                                break;
//                        }
//
//                        elseBlock = (Block) ifStatement.getElseStatement();
//
//                        if (elseBlock != null && elseBlock.statements().size() > 0) {
//                            Node node = new Node("", "", compilationUnit.getLineNumber(preStatement.getStartPosition()),
//                                    0, 0, 0);
//                            if (graph.containsEdge(node, preNode)) {
//                                graph.removeEdge(node, preNode);
//                            }
//                        }
//
//                    }
//
//                }
//            }


            if (statement.getNodeType() == ASTNode.IF_STATEMENT) {
                IfStatement ifStatement = (IfStatement) statement;
                handleIfThenBlock(preNode, nextToCondition, ifStatement);

                Statement ifStatementElseBlock = ifStatement.getElseStatement();

                handleIfElseBlock(ifStatement, ifStatementElseBlock, nextToCondition);
            }

        }
    }

    private void handleIfElseBlock(IfStatement ifStatement, Statement ifStatementElseBlock, Node nextToCondition) {
        boolean elseHasBlock = false;

        if (!(ifStatementElseBlock instanceof EmptyStatement)) {
            if (ifStatementElseBlock instanceof IfStatement) {
                if (!graph.containsVertex(ConvertToGraphNode.convertToGraphNode(ifStatementElseBlock)))
                    graph.addVertex(ConvertToGraphNode.convertToGraphNode(ifStatementElseBlock));

                graph.addEdge(ConvertToGraphNode.convertToGraphNode(ifStatement),
                        ConvertToGraphNode.convertToGraphNode(ifStatementElseBlock), new Edge(ConvertToGraphNode.convertToGraphNode(ifStatement), ConvertToGraphNode.convertToGraphNode(ifStatementElseBlock), ""));

                elseHasBlock = true;
                handleIfThenBlock(ifStatementElseBlock, nextToCondition, (IfStatement) ifStatementElseBlock);
                handleIfElseBlock((IfStatement) ifStatementElseBlock, ((IfStatement) ifStatementElseBlock).getElseStatement(), nextToCondition);
            } else {

                if (ifStatementElseBlock instanceof Block) {
                    Block elseBlock = (Block) ifStatementElseBlock;
                    if (elseBlock != null) {
                        if (elseBlock.statements().size() > 0) {
                            new BlockHandler(elseBlock).handle(ifStatement, ifStatement, nextToCondition);
                            elseHasBlock = true;
                            Statement statement1 = (Statement) elseBlock.statements().get(elseBlock.statements().size() - 1);

//                        if(!((statement1 instanceof IfStatement) &&
//                        graph.outDegreeOf(ConvertToGraphNode.convertToGraphNode(statement1)) >= 2) )
                            graph.addEdge(ConvertToGraphNode.convertToGraphNode(statement1), nextToCondition, new Edge(ConvertToGraphNode.convertToGraphNode(statement1), nextToCondition, ""));
                        }
                    }

                } else if (ifStatementElseBlock instanceof ExpressionStatement) {
                    elseHasBlock = true;
                    ExpressionStatement expressionStatement = (ExpressionStatement) ifStatementElseBlock;
                    Block block = compilationUnit.getAST().newBlock();
                    block.statements().add(ASTNode.copySubtree(compilationUnit.getAST(), expressionStatement));
                    new BlockHandler(block).handle(ifStatement, ifStatement, nextToCondition);
                    graph.addEdge(ConvertToGraphNode.convertToGraphNode(expressionStatement), nextToCondition, new Edge(ConvertToGraphNode.convertToGraphNode(expressionStatement), nextToCondition, ""));

                }

            }
        }
        if (!elseHasBlock)
            graph.addEdge(ConvertToGraphNode.convertToGraphNode(ifStatement), nextToCondition,
                    new Edge(ConvertToGraphNode.convertToGraphNode(ifStatement), nextToCondition, ""));

    }

    private void handleIfThenBlock(ASTNode preNode, Node nextToCondition, IfStatement ifStatement) {
        Statement ifStatementThenBlock = ifStatement.getThenStatement();

        boolean hasBlock = false;

        if (!(ifStatementThenBlock instanceof EmptyStatement)) {
            if (ifStatementThenBlock instanceof Block) {
                Block thenBlock = (Block) ifStatementThenBlock;
                if (thenBlock != null) {
                    if (thenBlock.statements().size() > 0) {
                        new BlockHandler(thenBlock).handle(preNode, ifStatement, nextToCondition);
                        hasBlock = true;

                        Statement statement1 = (Statement) thenBlock.statements().get(thenBlock.statements().size() - 1);
                        if (!((statement1 instanceof IfStatement) &&
                                graph.outDegreeOf(ConvertToGraphNode.convertToGraphNode(statement1)) >= 2))
                            graph.addEdge(ConvertToGraphNode.convertToGraphNode(statement1), nextToCondition, new Edge(ConvertToGraphNode.convertToGraphNode(statement1), nextToCondition, ""));
                    }
                }
            } else {
                if (ifStatementThenBlock instanceof ExpressionStatement) {
                    ExpressionStatement expressionStatement = (ExpressionStatement) ifStatementThenBlock;
                    Block block = compilationUnit.getAST().newBlock();
                    block.statements().add(ASTNode.copySubtree(compilationUnit.getAST(), expressionStatement));
                    hasBlock = true;
                    new BlockHandler(block).handle(preNode, ifStatement, nextToCondition);
                    graph.addEdge(ConvertToGraphNode.convertToGraphNode(expressionStatement), nextToCondition, new Edge(ConvertToGraphNode.convertToGraphNode(expressionStatement), nextToCondition, ""));
                } else if (ifStatementThenBlock instanceof IfStatement) {
                    handleIfThenBlock(ifStatementThenBlock, nextToCondition, (IfStatement) ifStatementThenBlock);
                }
            }
        }

        if (!hasBlock)
            graph.addEdge(ConvertToGraphNode.convertToGraphNode(ifStatement), nextToCondition,
                    new Edge(ConvertToGraphNode.convertToGraphNode(ifStatement), nextToCondition, ""));
    }
}

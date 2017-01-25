package com.ferooz.sourcecodearchitect;

import com.ferooz.sourcecodearchitect.graph.Edge;
import com.ferooz.sourcecodearchitect.graph.Node;
import com.ferooz.sourcecodearchitect.handlers.BlockHandler;
import com.ferooz.sourcecodearchitect.handlers.Handler;
import com.ferooz.sourcecodearchitect.util.ASTUtil;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.IOException;

/**
 * Created by Ferooz on 12/01/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {


        Handler.compilationUnit = ASTUtil.createCompilationUnit("/Users/Ferooz/Mine/Projects/sourcecodearchitect/SourceCodeArchitect/src/main/java/com/ferooz/sourcecodearchitect/util/Test.java",
                new String[]{"/Users/Ferooz/Documents/Workspaces/TestJdeo"}, null);

        TypeDeclaration typeDeclaration = (TypeDeclaration) Handler.compilationUnit.types().get(0);

        ITypeBinding typeDeclarationBinding = typeDeclaration.resolveBinding();


        System.out.println(typeDeclaration.properties());
        if(typeDeclarationBinding.isLocal()) {
            ITypeBinding declaringClass = typeDeclarationBinding.getDeclaringClass();
            String className = declaringClass.getQualifiedName() + "." + typeDeclarationBinding.getName();
            System.out.println(className);
        }
        else {
            System.out.println(typeDeclarationBinding.getQualifiedName());
        }

        //        for(MethodDeclaration methodDeclaration : typeDeclaration.getMethods()) {
//            Node entryNode = new Node("", "", Handler.compilationUnit.getLineNumber(methodDeclaration.getStartPosition()),
//                    methodDeclaration.getStartPosition(), methodDeclaration.getLength(),0);
//            Node exitNode = new Node("EXIT", "", 999, 0,0,0);
//            Handler.graph.addVertex(entryNode);
//            Handler.graph.addVertex(exitNode);
//
//
//            BlockHandler blockHandler = new BlockHandler(methodDeclaration.getBody());
//            blockHandler.handle(methodDeclaration, methodDeclaration, exitNode);
//        }

//        String digraph = "digraph G { \n";
//
//        for(Edge edge : Handler.graph.edgeSet()){
//            digraph += "\"" + edge.getV1().startLine + "\" -> \"" +  edge.getV2().startLine + "\"\n";
////            System.out.println(edge);
//        }

//        digraph += "}";
//
//        System.out.println(digraph);
    }
}

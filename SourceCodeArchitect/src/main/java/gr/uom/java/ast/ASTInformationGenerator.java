package gr.uom.java.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTInformationGenerator {

	private static CompilationUnit compilationUnit;
	
	public static void setCurrentCompilationUnit(CompilationUnit typeRoot) {
		compilationUnit = typeRoot;
	}

	public static ASTInformation generateASTInformation(ASTNode astNode) {
		return new ASTInformation(compilationUnit, astNode);
	}
}

package gr.uom.java.ast;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

public class CompilationUnitCache {

	private static CompilationUnitCache instance;
	public static List<CompilationUnit> compilationUnitList;

	static {
		compilationUnitList = new ArrayList<>();
	}

	public static CompilationUnitCache getInstance() {
		if(instance == null) {
			instance = new CompilationUnitCache();
		}
		return instance;
	}

	public CompilationUnit getCompilationUnit(CompilationUnit compilationUnit1) {
		return compilationUnitList.get(compilationUnitList.indexOf(compilationUnit1));
	}

}

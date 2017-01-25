package gr.uom.java.ast;

import gr.uom.java.ast.decomposition.cfg.AbstractVariable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CompilationUnitCache extends Indexer {

	private static CompilationUnitCache instance;
	private LinkedList<ITypeRoot> iTypeRootList;
	private LinkedList<CompilationUnit> compilationUnitList;
	private List<ITypeRoot> lockedTypeRoots;

	//String key corresponds to MethodDeclaration.resolveBinding.getKey()
	private Map<String, LinkedHashSet<AbstractVariable>> usedFieldsForMethodExpressionMap;
	//String key corresponds to MethodDeclaration.resolveBinding.getKey()
	private Map<String, LinkedHashSet<AbstractVariable>> definedFieldsForMethodExpressionMap;
	//String key corresponds to MethodDeclaration.resolveBinding.getKey()
	private Map<String, LinkedHashSet<String>> thrownExceptionTypesForMethodExpressionMap;

	public void addUsedFieldForMethodExpression(AbstractVariable field, AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		if(usedFieldsForMethodExpressionMap.containsKey(methodBindingKey)) {
			LinkedHashSet<AbstractVariable> fields = usedFieldsForMethodExpressionMap.get(methodBindingKey);
			fields.add(field);
		}
		else {
			LinkedHashSet<AbstractVariable> fields = new LinkedHashSet<AbstractVariable>();
			fields.add(field);
			usedFieldsForMethodExpressionMap.put(methodBindingKey, fields);
		}
	}

	public void setEmptyUsedFieldsForMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		LinkedHashSet<AbstractVariable> usedFields = new LinkedHashSet<AbstractVariable>();
		usedFieldsForMethodExpressionMap.put(methodBindingKey, usedFields);
	}

	public void addDefinedFieldForMethodExpression(AbstractVariable field, AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		if(definedFieldsForMethodExpressionMap.containsKey(methodBindingKey)) {
			LinkedHashSet<AbstractVariable> fields = definedFieldsForMethodExpressionMap.get(methodBindingKey);
			fields.add(field);
		}
		else {
			LinkedHashSet<AbstractVariable> fields = new LinkedHashSet<AbstractVariable>();
			fields.add(field);
			definedFieldsForMethodExpressionMap.put(methodBindingKey, fields);
		}
	}

	public void setEmptyDefinedFieldsForMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		LinkedHashSet<AbstractVariable> usedFields = new LinkedHashSet<AbstractVariable>();
		definedFieldsForMethodExpressionMap.put(methodBindingKey, usedFields);
	}

	public void setThrownExceptionTypesForMethodExpression(AbstractMethodDeclaration mo, LinkedHashSet<String> thrownExceptionTypes) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		thrownExceptionTypesForMethodExpressionMap.put(methodBindingKey, thrownExceptionTypes);
	}

	public boolean containsMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		if(usedFieldsForMethodExpressionMap.containsKey(methodBindingKey))
			return true;
		if(definedFieldsForMethodExpressionMap.containsKey(methodBindingKey))
			return true;
		return false;
	}

	public Set<AbstractVariable> getUsedFieldsForMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		return usedFieldsForMethodExpressionMap.get(methodBindingKey);
	}

	public Set<AbstractVariable> getDefinedFieldsForMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		return definedFieldsForMethodExpressionMap.get(methodBindingKey);
	}

	public Set<String> getThrownExceptionTypesForMethodExpression(AbstractMethodDeclaration mo) {
		String methodBindingKey = mo.getMethodDeclaration().resolveBinding().getKey();
		return thrownExceptionTypesForMethodExpressionMap.get(methodBindingKey);
	}

	private CompilationUnitCache() {
		super();
		this.iTypeRootList = new LinkedList<ITypeRoot>();
		this.lockedTypeRoots = new ArrayList<ITypeRoot>();
		this.compilationUnitList = new LinkedList<CompilationUnit>();
		this.usedFieldsForMethodExpressionMap = new HashMap<String, LinkedHashSet<AbstractVariable>>();
		this.definedFieldsForMethodExpressionMap = new HashMap<String, LinkedHashSet<AbstractVariable>>();
		this.thrownExceptionTypesForMethodExpressionMap = new HashMap<String, LinkedHashSet<String>>();
	}

	public static CompilationUnitCache getInstance() {
		if(instance == null) {
			instance = new CompilationUnitCache();
		}
		return instance;
	}

	public CompilationUnit getCompilationUnit(ITypeRoot iTypeRoot) {
		if(iTypeRoot instanceof IClassFile) {
			IClassFile classFile = (IClassFile)iTypeRoot;
			return LibraryClassStorage.getInstance().getCompilationUnit(classFile);
		}
		else {
			if(iTypeRootList.contains(iTypeRoot)) {
				int position = iTypeRootList.indexOf(iTypeRoot);
				return compilationUnitList.get(position);
			}
			else {
				ASTParser parser = ASTParser.newParser(ASTReader.JLS);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setSource(iTypeRoot);
				parser.setResolveBindings(true);
				CompilationUnit compilationUnit = (CompilationUnit)parser.createAST(null);
				
				int maximumCacheSize = 10000000;
				if(iTypeRootList.size() < maximumCacheSize) {
					iTypeRootList.add(iTypeRoot);
					compilationUnitList.add(compilationUnit);
				}
				else {
					if(!lockedTypeRoots.isEmpty()) {
						int indexToBeRemoved = 0;
						int counter = 0;
						for(ITypeRoot lockedTypeRoot : lockedTypeRoots) {
							if(iTypeRootList.get(counter).equals(lockedTypeRoot)) {
								indexToBeRemoved++;
							}
							counter++;
						}
						iTypeRootList.remove(indexToBeRemoved);
						compilationUnitList.remove(indexToBeRemoved);
					}
					else {
						iTypeRootList.removeFirst();
						compilationUnitList.removeFirst();
					}
					iTypeRootList.add(iTypeRoot);
					compilationUnitList.add(compilationUnit);
				}
				return compilationUnit;
			}
		}
	}

	public void lock(ITypeRoot iTypeRoot) {
		if(!lockedTypeRoots.contains(iTypeRoot))
			lockedTypeRoots.add(iTypeRoot);
	}

	public void releaseLock() {
		lockedTypeRoots.clear();
		usedFieldsForMethodArgumentsMap.clear();
		definedFieldsForMethodArgumentsMap.clear();
		usedFieldsForMethodExpressionMap.clear();
		definedFieldsForMethodExpressionMap.clear();
	}

	public void clearCache() {
		lockedTypeRoots.clear();
		iTypeRootList.clear();
		compilationUnitList.clear();
	}
}

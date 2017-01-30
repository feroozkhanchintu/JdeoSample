package gr.uom.java.ast;

import gr.uom.java.ast.decomposition.AbstractStatement;
import gr.uom.java.ast.decomposition.MethodBodyObject;
import gr.uom.java.ast.decomposition.StatementObject;
import gr.uom.java.ast.decomposition.cfg.AbstractVariable;
import gr.uom.java.ast.decomposition.cfg.PlainVariable;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class MethodObject implements AbstractMethodDeclaration {

    private TypeObject returnType;
    private boolean _abstract;
    private boolean _static;
    private boolean _synchronized;
    private boolean _native;
    private ConstructorObject constructorObject;
    private boolean testAnnotation;
    private volatile int hashCode = 0;

    public MethodObject(ConstructorObject co) {
        this.constructorObject = co;
        this._abstract = false;
        this._static = false;
        this._synchronized = false;
        this._native = false;
        this.testAnnotation = false;
    }

    public void setReturnType(TypeObject returnType) {
        this.returnType = returnType;
    }

    public TypeObject getReturnType() {
        return returnType;
    }

    public void setAbstract(boolean abstr) {
        this._abstract = abstr;
    }

    public boolean isAbstract() {
        return this._abstract;
    }

    public boolean isStatic() {
        return _static;
    }

    public void setStatic(boolean s) {
        _static = s;
    }

    public boolean isSynchronized() {
    	return this._synchronized;
    }

    public void setSynchronized(boolean s) {
    	this._synchronized = s;
    }

    public boolean isNative() {
    	return this._native;
    }

    public void setNative(boolean n) {
    	this._native = n;
    }

    public String getName() {
        return constructorObject.getName();
    }

    public boolean hasTestAnnotation() {
		return testAnnotation;
	}

	public void setTestAnnotation(boolean testAnnotation) {
		this.testAnnotation = testAnnotation;
	}

    public Set<String> getExceptionsInJavaDocThrows() {
		return constructorObject.getExceptionsInJavaDocThrows();
	}

	public Access getAccess() {
        return constructorObject.getAccess();
    }

    public MethodDeclaration getMethodDeclaration() {
    	return constructorObject.getMethodDeclaration();
    }

    public MethodBodyObject getMethodBody() {
    	return constructorObject.getMethodBody();
    }

    public MethodInvocationObject generateMethodInvocation() {
    	return new MethodInvocationObject(TypeObject.extractTypeObject(this.constructorObject.className), this.constructorObject.name, this.returnType, this.constructorObject.getParameterTypeList());
    }

    public SuperMethodInvocationObject generateSuperMethodInvocation() {
    	return new SuperMethodInvocationObject(TypeObject.extractTypeObject(this.constructorObject.className), this.constructorObject.name, this.returnType, this.constructorObject.getParameterTypeList());
    }

    public FieldInstructionObject isGetter() {
    	if(getMethodBody() != null) {
	    	List<AbstractStatement> abstractStatements = getMethodBody().getCompositeStatement().getStatements();
	    	if(abstractStatements.size() == 1 && abstractStatements.get(0) instanceof StatementObject) {
	    		StatementObject statementObject = (StatementObject)abstractStatements.get(0);
	    		Statement statement = statementObject.getStatement();
	    		if(statement instanceof ReturnStatement) {
	    			ReturnStatement returnStatement = (ReturnStatement) statement;
	    			if((returnStatement.getExpression() instanceof SimpleName || returnStatement.getExpression() instanceof FieldAccess) && statementObject.getFieldInstructions().size() == 1 && statementObject.getMethodInvocations().size() == 0 &&
		    				statementObject.getLocalVariableDeclarations().size() == 0 && statementObject.getLocalVariableInstructions().size() == 0 && this.constructorObject.parameterList.size() == 0) {
	    				return statementObject.getFieldInstructions().get(0);
	    			}
	    		}
	    	}
    	}
    	return null;
    }

    public FieldInstructionObject isSetter() {
    	if(getMethodBody() != null) {
	    	List<AbstractStatement> abstractStatements = getMethodBody().getCompositeStatement().getStatements();
	    	if(abstractStatements.size() == 1 && abstractStatements.get(0) instanceof StatementObject) {
	    		StatementObject statementObject = (StatementObject)abstractStatements.get(0);
	    		Statement statement = statementObject.getStatement();
	    		if(statement instanceof ExpressionStatement) {
	    			ExpressionStatement expressionStatement = (ExpressionStatement)statement;
	    			if(expressionStatement.getExpression() instanceof Assignment && statementObject.getFieldInstructions().size() == 1 && statementObject.getMethodInvocations().size() == 0 &&
	        				statementObject.getLocalVariableDeclarations().size() == 0 && statementObject.getLocalVariableInstructions().size() == 1 && this.constructorObject.parameterList.size() == 1) {
	    				Assignment assignment = (Assignment)expressionStatement.getExpression();
	    				if((assignment.getLeftHandSide() instanceof SimpleName || assignment.getLeftHandSide() instanceof FieldAccess) && assignment.getRightHandSide() instanceof SimpleName)
	    					return statementObject.getFieldInstructions().get(0);
	    			}
	    		}
	    	}
    	}
    	return null;
    }

    public FieldInstructionObject isCollectionAdder() {
    	if(getMethodBody() != null) {
	    	List<AbstractStatement> abstractStatements = getMethodBody().getCompositeStatement().getStatements();
	    	if(abstractStatements.size() == 1 && abstractStatements.get(0) instanceof StatementObject) {
	    		StatementObject statementObject = (StatementObject)abstractStatements.get(0);
	    		if(statementObject.getFieldInstructions().size() == 1 && statementObject.getMethodInvocations().size() == 1 &&
	    				statementObject.getLocalVariableDeclarations().size() == 0 && statementObject.getLocalVariableInstructions().size() == 1 && this.constructorObject.parameterList.size() == 1) {
	    			String methodName = statementObject.getMethodInvocations().get(0).getMethodName();
	    			String originClassName = statementObject.getMethodInvocations().get(0).getOriginClassName();
	    			List<String> acceptableOriginClassNames = new ArrayList<String>();
	    			acceptableOriginClassNames.add("java.util.Collection");
	    			acceptableOriginClassNames.add("java.util.AbstractCollection");
	    			acceptableOriginClassNames.add("java.util.List");
	    			acceptableOriginClassNames.add("java.util.AbstractList");
	    			acceptableOriginClassNames.add("java.util.ArrayList");
	    			acceptableOriginClassNames.add("java.util.LinkedList");
	    			acceptableOriginClassNames.add("java.util.Set");
	    			acceptableOriginClassNames.add("java.util.AbstractSet");
	    			acceptableOriginClassNames.add("java.util.HashSet");
	    			acceptableOriginClassNames.add("java.util.LinkedHashSet");
	    			acceptableOriginClassNames.add("java.util.SortedSet");
	    			acceptableOriginClassNames.add("java.util.TreeSet");
	    			acceptableOriginClassNames.add("java.util.Vector");
	    			if(methodName.equals("add") || methodName.equals("addElement") || methodName.equals("addAll")) {
	    				if(acceptableOriginClassNames.contains(originClassName))
	    					return statementObject.getFieldInstructions().get(0);
	    			}
	    		}
	    	}
    	}
    	return null;
    }

    public String getClassName() {
        return constructorObject.getClassName();
    }

    public ListIterator<ParameterObject> getParameterListIterator() {
        return constructorObject.getParameterListIterator();
    }

    public ParameterObject getParameter(int position) {
    	return constructorObject.getParameter(position);
    }

    public List<MethodInvocationObject> getMethodInvocations() {
        return constructorObject.getMethodInvocations();
    }

    public List<SuperMethodInvocationObject> getSuperMethodInvocations() {
        return constructorObject.getSuperMethodInvocations();
    }

    public List<ConstructorInvocationObject> getConstructorInvocations() {
        return constructorObject.getConstructorInvocations();
    }

    public List<FieldInstructionObject> getFieldInstructions() {
        return constructorObject.getFieldInstructions();
    }

    public List<SuperFieldInstructionObject> getSuperFieldInstructions() {
    	return constructorObject.getSuperFieldInstructions();
    }

    public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
        return constructorObject.getLocalVariableDeclarations();
    }
    
    public List<LocalVariableInstructionObject> getLocalVariableInstructions() {
        return constructorObject.getLocalVariableInstructions();
    }

	public List<CreationObject> getCreations() {
		return constructorObject.getCreations();
	}

	public List<LiteralObject> getLiterals() {
		return constructorObject.getLiterals();
	}

	public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
		return constructorObject.getAnonymousClassDeclarations();
	}

	public Set<String> getExceptionsInThrowStatements() {
		return constructorObject.getExceptionsInThrowStatements();
	}

    public boolean containsMethodInvocation(MethodInvocationObject methodInvocation) {
    	return constructorObject.containsMethodInvocation(methodInvocation);
    }

    public boolean containsFieldInstruction(FieldInstructionObject fieldInstruction) {
    	return constructorObject.containsFieldInstruction(fieldInstruction);
    }

    public boolean containsSuperMethodInvocation(SuperMethodInvocationObject superMethodInvocation) {
    	return constructorObject.containsSuperMethodInvocation(superMethodInvocation);
    }

	public Map<AbstractVariable, LinkedHashSet<MethodInvocationObject>> getInvokedMethodsThroughFields() {
		return constructorObject.getInvokedMethodsThroughFields();
	}

	public Map<AbstractVariable, ArrayList<MethodInvocationObject>> getNonDistinctInvokedMethodsThroughFields() {
		return constructorObject.getNonDistinctInvokedMethodsThroughFields();
	}

	public Map<AbstractVariable, LinkedHashSet<MethodInvocationObject>> getInvokedMethodsThroughParameters() {
		return constructorObject.getInvokedMethodsThroughParameters();
	}

	public Map<AbstractVariable, ArrayList<MethodInvocationObject>> getNonDistinctInvokedMethodsThroughParameters() {
		return constructorObject.getNonDistinctInvokedMethodsThroughParameters();
	}

	public Map<AbstractVariable, LinkedHashSet<MethodInvocationObject>> getInvokedMethodsThroughLocalVariables() {
		return constructorObject.getInvokedMethodsThroughLocalVariables();
	}

	public Set<MethodInvocationObject> getInvokedMethodsThroughThisReference() {
		return constructorObject.getInvokedMethodsThroughThisReference();
	}

	public List<MethodInvocationObject> getNonDistinctInvokedMethodsThroughThisReference() {
		return constructorObject.getNonDistinctInvokedMethodsThroughThisReference();
	}

	public Set<MethodInvocationObject> getInvokedStaticMethods() {
		return constructorObject.getInvokedStaticMethods();
	}

	public Set<AbstractVariable> getDefinedFieldsThroughFields() {
		return constructorObject.getDefinedFieldsThroughFields();
	}

	public Set<AbstractVariable> getUsedFieldsThroughFields() {
		return constructorObject.getUsedFieldsThroughFields();
	}

	public List<AbstractVariable> getNonDistinctDefinedFieldsThroughFields() {
		return constructorObject.getNonDistinctDefinedFieldsThroughFields();
	}

	public List<AbstractVariable> getNonDistinctUsedFieldsThroughFields() {
		return constructorObject.getNonDistinctUsedFieldsThroughFields();
	}

	public Set<AbstractVariable> getDefinedFieldsThroughParameters() {
		return constructorObject.getDefinedFieldsThroughParameters();
	}

	public Set<AbstractVariable> getUsedFieldsThroughParameters() {
		return constructorObject.getUsedFieldsThroughParameters();
	}

	public List<AbstractVariable> getNonDistinctDefinedFieldsThroughParameters() {
		return constructorObject.getNonDistinctDefinedFieldsThroughParameters();
	}

	public List<AbstractVariable> getNonDistinctUsedFieldsThroughParameters() {
		return constructorObject.getNonDistinctUsedFieldsThroughParameters();
	}

	public Set<AbstractVariable> getDefinedFieldsThroughLocalVariables() {
		return constructorObject.getDefinedFieldsThroughLocalVariables();
	}

	public Set<AbstractVariable> getUsedFieldsThroughLocalVariables() {
		return constructorObject.getUsedFieldsThroughLocalVariables();
	}

	public Set<PlainVariable> getDefinedFieldsThroughThisReference() {
		return constructorObject.getDefinedFieldsThroughThisReference();
	}

	public List<PlainVariable> getNonDistinctDefinedFieldsThroughThisReference() {
		return constructorObject.getNonDistinctDefinedFieldsThroughThisReference();
	}

	public Set<PlainVariable> getUsedFieldsThroughThisReference() {
		return constructorObject.getUsedFieldsThroughThisReference();
	}

	public List<PlainVariable> getNonDistinctUsedFieldsThroughThisReference() {
		return constructorObject.getNonDistinctUsedFieldsThroughThisReference();
	}

	public Set<PlainVariable> getDeclaredLocalVariables() {
		return constructorObject.getDeclaredLocalVariables();
	}

	public Set<PlainVariable> getDefinedLocalVariables() {
		return constructorObject.getDefinedLocalVariables();
	}

	public Set<PlainVariable> getUsedLocalVariables() {
		return constructorObject.getUsedLocalVariables();
	}

	public Map<PlainVariable, LinkedHashSet<MethodInvocationObject>> getParametersPassedAsArgumentsInMethodInvocations() {
		return constructorObject.getParametersPassedAsArgumentsInMethodInvocations();
	}

	public Map<PlainVariable, LinkedHashSet<SuperMethodInvocationObject>> getParametersPassedAsArgumentsInSuperMethodInvocations() {
		return constructorObject.getParametersPassedAsArgumentsInSuperMethodInvocations();
	}

	public Map<PlainVariable, LinkedHashSet<ConstructorInvocationObject>> getParametersPassedAsArgumentsInConstructorInvocations() {
		return constructorObject.getParametersPassedAsArgumentsInConstructorInvocations();
	}

    public boolean containsSuperMethodInvocation() {
    	return constructorObject.containsSuperMethodInvocation();
    }

    public boolean containsSuperFieldAccess() {
    	return constructorObject.containsSuperFieldAccess();
    }

    public List<TypeObject> getParameterTypeList() {
    	return constructorObject.getParameterTypeList();
    }

    public List<String> getParameterList() {
    	return constructorObject.getParameterList();
    }

    public boolean equals(MethodInvocationObject mio) {
    	return this.getClassName().equals(mio.getOriginClassName()) && this.getName().equals(mio.getMethodName()) &&
    		this.returnType.equalsClassType(mio.getReturnType()) && equalParameterTypes(this.constructorObject.getParameterTypeList(), mio.getParameterTypeList());
    		/*this.constructorObject.getParameterTypeList().equals(mio.getParameterTypeList());*/
    }

    public boolean equals(SuperMethodInvocationObject smio) {
    	return this.getClassName().equals(smio.getOriginClassName()) && this.getName().equals(smio.getMethodName()) &&
    		this.returnType.equalsClassType(smio.getReturnType()) && equalParameterTypes(this.constructorObject.getParameterTypeList(), smio.getParameterTypeList());
    		/*this.constructorObject.getParameterTypeList().equals(smio.getParameterTypeList());*/
    }

    private boolean equalParameterTypes(List<TypeObject> list1, List<TypeObject> list2) {
    	if(list1.size() != list2.size())
    		return false;
    	for(int i=0; i<list1.size(); i++) {
    		TypeObject type1 = list1.get(i);
    		TypeObject type2 = list2.get(i);
    		if(!type1.equalsClassType(type2))
    			return false;
    		//array dimension comparison is skipped if at least one of the class types is a type parameter name, such as E, K, N, T, V, S, U
    		if(type1.getArrayDimension() != type2.getArrayDimension() && type1.getClassType().length() != 1 && type2.getClassType().length() != 1)
    			return false;
    	}
    	return true;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof MethodObject) {
            MethodObject methodObject = (MethodObject)o;

            return this.returnType.equals(methodObject.returnType) &&
                this.constructorObject.equals(methodObject.constructorObject);
        }
        return false;
    }

    public int hashCode() {
    	if(hashCode == 0) {
    		int result = 17;
    		result = 37*result + returnType.hashCode();
    		result = 37*result + constructorObject.hashCode();
    		hashCode = result;
    	}
    	return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!constructorObject.access.equals(Access.NONE))
            sb.append(constructorObject.access.toString()).append(" ");
        if(_abstract)
            sb.append("abstract").append(" ");
        if(_static)
            sb.append("static").append(" ");
        sb.append(returnType.toString()).append(" ");
        sb.append(constructorObject.name);
        sb.append("(");
        if(!constructorObject.parameterList.isEmpty()) {
            for(int i=0; i<constructorObject.parameterList.size()-1; i++)
                sb.append(constructorObject.parameterList.get(i).toString()).append(", ");
            sb.append(constructorObject.parameterList.get(constructorObject.parameterList.size()-1).toString());
        }
        sb.append(")");
        /*if(constructorObject.methodBody != null)
        	sb.append("\n").append(constructorObject.methodBody.toString());*/
        return sb.toString();
    }

    public String getSignature() {
    	return constructorObject.getSignature();
    }
}
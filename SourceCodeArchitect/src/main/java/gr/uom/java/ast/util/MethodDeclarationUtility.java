package gr.uom.java.ast.util;

import gr.uom.java.ast.decomposition.cfg.AbstractVariable;
import gr.uom.java.ast.decomposition.cfg.CompositeVariable;
import gr.uom.java.ast.decomposition.cfg.PlainVariable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodDeclarationUtility {

	public static AbstractVariable createVariable(SimpleName simpleName, AbstractVariable rightPart) {
		IBinding binding = simpleName.resolveBinding();
		if(binding != null && binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding variableBinding = (IVariableBinding)binding;
			AbstractVariable currentVariable = null;
			if(rightPart == null)
				currentVariable = new PlainVariable(variableBinding);
			else
				currentVariable = new CompositeVariable(variableBinding, rightPart);
			
			if(simpleName.getParent() instanceof QualifiedName) {
				QualifiedName qualifiedName = (QualifiedName)simpleName.getParent();
				Name qualifier = qualifiedName.getQualifier();
				if(qualifier instanceof SimpleName) {
					SimpleName qualifierSimpleName = (SimpleName)qualifier;
					if(!qualifierSimpleName.equals(simpleName))
						return createVariable(qualifierSimpleName, currentVariable);
					else
						return currentVariable;
				}
				else if(qualifier instanceof QualifiedName) {
					QualifiedName qualifiedName2 = (QualifiedName)qualifier;
					return createVariable(qualifiedName2.getName(), currentVariable);
				}
			}
			else if(simpleName.getParent() instanceof FieldAccess) {
				FieldAccess fieldAccess = (FieldAccess)simpleName.getParent();
				Expression fieldAccessExpression = fieldAccess.getExpression();
				if(fieldAccessExpression instanceof FieldAccess) {
					FieldAccess fieldAccess2 = (FieldAccess)fieldAccessExpression;
					return createVariable(fieldAccess2.getName(), currentVariable);
				}
				else if(fieldAccessExpression instanceof SimpleName) {
					SimpleName fieldAccessSimpleName = (SimpleName)fieldAccessExpression;
					return createVariable(fieldAccessSimpleName, currentVariable);
				}
				else if(fieldAccessExpression instanceof ThisExpression) {
					return currentVariable;
				}
			}
			else {
				return currentVariable;
			}
		}
		return null;
	}

	public static AbstractVariable processMethodInvocationExpression(Expression expression) {
		if(expression != null) {
			if(expression instanceof QualifiedName) {
				QualifiedName qualifiedName = (QualifiedName)expression;
				return createVariable(qualifiedName.getName(), null);
			}
			else if(expression instanceof FieldAccess) {
				FieldAccess fieldAccess = (FieldAccess)expression;
				return createVariable(fieldAccess.getName(), null);
			}
			else if(expression instanceof SimpleName) {
				SimpleName simpleName = (SimpleName)expression;
				return createVariable(simpleName, null);
			}
		}
		return null;
	}


	public static SimpleName getRightMostSimpleName(Expression expression) {
		SimpleName simpleName = null;
		if(expression instanceof SimpleName) {
			simpleName = (SimpleName)expression;
		}
		else if(expression instanceof QualifiedName) {
			QualifiedName leftHandSideQualifiedName = (QualifiedName)expression;
			simpleName = leftHandSideQualifiedName.getName();
		}
		else if(expression instanceof FieldAccess) {
			FieldAccess leftHandSideFieldAccess = (FieldAccess)expression;
			simpleName = leftHandSideFieldAccess.getName();
		}
		else if(expression instanceof ArrayAccess) {
			ArrayAccess leftHandSideArrayAccess = (ArrayAccess)expression;
			Expression array = leftHandSideArrayAccess.getArray();
			if(array instanceof SimpleName) {
				simpleName = (SimpleName)array;
			}
			else if(array instanceof QualifiedName) {
				QualifiedName arrayQualifiedName = (QualifiedName)array;
				simpleName = arrayQualifiedName.getName();
			}
			else if(array instanceof FieldAccess) {
				FieldAccess arrayFieldAccess = (FieldAccess)array;
				simpleName = arrayFieldAccess.getName();
			}
		}
		return simpleName;
	}
}

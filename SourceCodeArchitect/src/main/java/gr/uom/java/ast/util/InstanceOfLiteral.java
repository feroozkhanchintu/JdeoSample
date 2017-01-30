package gr.uom.java.ast.util;

import org.eclipse.jdt.core.dom.*;

public class InstanceOfLiteral implements ExpressionInstanceChecker {

	public boolean instanceOf(Expression expression) {
		if(expression instanceof BooleanLiteral || expression instanceof CharacterLiteral || expression instanceof StringLiteral ||
				expression instanceof NullLiteral || expression instanceof NumberLiteral || expression instanceof TypeLiteral)
			return true;
		else
			return false;
	}

}

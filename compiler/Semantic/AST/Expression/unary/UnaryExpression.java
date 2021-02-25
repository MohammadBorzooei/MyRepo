package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.variable.SimpleVariable;
import Semantic.AST.Expression.variable.Variable;
import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.Type;
import Semantic.AST.Type.CheckType;


public abstract class UnaryExpression extends Expression {
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    protected Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    protected void checkIsConstant(Variable variable) {
        boolean isConstant = false;
        if (variable instanceof SimpleVariable) {
            DSCP dscp = SymbolTable.getInstance().getDescriptor(variable.getName());
            isConstant = dscp.isConstant();
        }
        if (isConstant)
            throw new RuntimeException("Can Not Assign To Constant Variable.");
    }

    public abstract int determineOp(Type resultType);

    @Override
    public Type getType() {
        return CheckType.unaryExprTypeCheck(expression.getType());
    }

}
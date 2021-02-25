package Semantic.AST.Statement.assignment;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.InitialExpression;
import Semantic.AST.Expression.StepExpression;
import Semantic.AST.Expression.variable.SimpleVariable;
import Semantic.AST.Expression.variable.Variable;
import Semantic.AST.Statement.Statement;
import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.SymbolTable.SymbolTable;

public abstract class Assignment extends Statement implements InitialExpression, StepExpression {
    protected Variable variable;
    protected Expression expression;

    public Assignment(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    protected void checkIsConst() {
        boolean isConst = false;
        if (variable instanceof SimpleVariable) {
            DSCP dscp = SymbolTable.getInstance().getDescriptor(variable.getName());
            isConst = dscp.isConstant();
        }
        if (isConst)
            throw new RuntimeException("Can Not Assign Any Value To The Constant Variable.");
    }
}

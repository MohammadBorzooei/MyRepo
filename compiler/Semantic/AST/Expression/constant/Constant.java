package Semantic.AST.Expression.constant;

import Semantic.AST.Expression.Expression;

public abstract class Constant extends Expression {
    public abstract Object getValue();
}

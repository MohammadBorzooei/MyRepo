package Semantic.AST.Expression.binary;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Type.CheckType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public abstract class BinaryExpression extends Expression {
    protected Expression expression1, expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public Type getType() {
        return CheckType.binaryExprTypeCheck(expression1.getType(), expression2.getType());
    }

    protected void codegenExpressions(Type resultType, ClassWriter cw, MethodVisitor mv) {
        expression1.codegen(cw, mv);
        expression1.castOperandType(resultType, mv);
        expression2.codegen(cw, mv);
        expression2.castOperandType(resultType, mv);
    }
}

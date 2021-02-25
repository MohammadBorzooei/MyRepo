package Semantic.AST.Expression.binary.conditional;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.BinaryExpression;
import org.objectweb.asm.*;


public abstract class ConditionalExpression extends BinaryExpression {
    int opCode;
    int compareCode;

    public ConditionalExpression(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    public abstract int determineOp(Type type);

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        Type type = getType();
        codegenExpressions(type, cw, mv);
        Label l1 = new Label();
        Label l2 = new Label();
        determineOp(type);
        if (type != Type.INT_TYPE)
            mv.visitInsn(compareCode);
        mv.visitJumpInsn(opCode, l1);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitJumpInsn(Opcodes.GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitLabel(l2);
    }

}
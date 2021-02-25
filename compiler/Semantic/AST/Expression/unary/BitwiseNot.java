package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Type.CheckType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IXOR;

public class BitwiseNot extends UnaryExpression {
    public BitwiseNot(Expression expression) {
        super(expression);
    }

    @Override
    public int determineOp(Type resultType) {
        return 0;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        Type type = getType();
        if (type != Type.INT_TYPE && type != Type.LONG_TYPE)
            throw new RuntimeException("Invalid Type For Not.");
        expression.codegen(cw, mv);
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(type.getOpcode(IXOR));
    }

    @Override
    public Type getType() {
        return CheckType.unaryExprTypeCheck(expression.getType());
    }
}
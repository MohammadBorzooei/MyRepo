package Semantic.AST.Expression.binary.conditional;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.BinaryExpression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class BitwiseXor extends BinaryExpression {


    public BitwiseXor(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        Type type = getType();
        if (!(type == Type.INT_TYPE || type == Type.LONG_TYPE))
            throw new IllegalArgumentException("BitwiseXor Used With Non-Integer Value.");
        codegenExpressions(type, cw, mv);
        mv.visitInsn(type.getOpcode(IXOR));
    }
}
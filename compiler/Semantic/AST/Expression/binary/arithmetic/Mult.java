package Semantic.AST.Expression.binary.arithmetic;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.BinaryExpression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class Mult extends BinaryExpression {
    public Mult(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        Type type = getType();
        codegenExpressions(type, cw, mv);
        mv.visitInsn(type.getOpcode(IMUL));
    }
}

package Semantic.AST.Expression.binary.arithmetic;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.BinaryExpression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class Plus extends BinaryExpression {
    public Plus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        Type type = getType();

        if (type.equals(Type.getType(String.class))) {

            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

            expression1.codegen(cw, mv);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" +
                    "Ljava/lang/String;" + ")Ljava/lang/StringBuilder;", false);

            expression2.codegen(cw, mv);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" +
                    "Ljava/lang/String;" + ")Ljava/lang/StringBuilder;", false);

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        } else {
            codegenExpressions(type, cw, mv);
            mv.visitInsn(type.getOpcode(IADD));
        }
    }
}
package Semantic.AST.Statement;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Println extends Statement {
    private final Expression expression;

    public Println(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (expression == null) {
            mv.visitLdcInsn("\n");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V", false);
        } else {
            expression.codegen(cw, mv);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(" + expression.getType() + ")V", false);
        }
    }
}

package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class PreMinusMinus extends PlusMinus {
    public PreMinusMinus(Expression expression) {
        super(expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        this.postOrPre = "pre";
        this.operator = "minus";
        super.codegen(cw, mv);
    }
}

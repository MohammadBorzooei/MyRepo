package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class PrePlusPlus extends PlusMinus {
    public PrePlusPlus(Expression expression) {
        super(expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        this.postOrPre = "pre";
        this.operator = "plus";
        super.codegen(cw, mv);
    }
}

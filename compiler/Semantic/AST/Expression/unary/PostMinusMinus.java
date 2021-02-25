package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class PostMinusMinus extends PlusMinus {
    public PostMinusMinus(Expression expression) {
        super(expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        this.postOrPre = "post";
        this.operator = "minus";
        super.codegen(cw, mv);
    }
}

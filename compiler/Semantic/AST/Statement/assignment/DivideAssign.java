package Semantic.AST.Statement.assignment;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.arithmetic.Divide;
import Semantic.AST.Expression.variable.Variable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class DivideAssign extends Assignment {
    public DivideAssign(Variable variable, Expression expression) {
        super(variable, expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        new Assign(variable, new Divide(variable, expression)).codegen(cw, mv);
    }
}

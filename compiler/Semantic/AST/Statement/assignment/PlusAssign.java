package Semantic.AST.Statement.assignment;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.arithmetic.Plus;
import Semantic.AST.Expression.variable.Variable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class PlusAssign extends Assignment {
    public PlusAssign(Variable variable, Expression expression) {
        super(variable, expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        new Assign(variable, new Plus(variable, expression)).codegen(cw, mv);
    }
}

package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.InitialExpression;
import Semantic.AST.Expression.StepExpression;
import Semantic.AST.Expression.constant.IntegerConstExp;
import Semantic.AST.Expression.variable.SimpleVariable;
import Semantic.AST.Expression.variable.Variable;
import Semantic.AST.Operation;
import Semantic.AST.Statement.assignment.MinusAssign;
import Semantic.AST.Statement.assignment.PlusAssign;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PlusMinus extends UnaryExpression implements InitialExpression, StepExpression, Operation {
    String postOrPre, operator;
    Type type;

    public PlusMinus(Expression expression) {
        super(expression);
    }

    @Override
    public int determineOp(Type resultType) {
        return 0;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        type = expression.getType();
        if (!(expression instanceof Variable) || (type != Type.INT_TYPE && type != Type.DOUBLE_TYPE && type != Type.LONG_TYPE && type != Type.FLOAT_TYPE))
            throw new IllegalArgumentException("Wrong Operand For -- Or ++ Operator.");
        Variable var = (Variable) expression;
        checkIsConstant(var);

        if (postOrPre.equals("post")) {
            new SimpleVariable(var.getName(), var.getType()).codegen(cw, mv);

            if (operator.equals("plus")) {
                new PlusAssign(var, new IntegerConstExp(1)).codegen(cw, mv);
            } else if (operator.equals("minus")) {
                new MinusAssign(var, new IntegerConstExp(1)).codegen(cw, mv);
            }
        } else if (postOrPre.equals("pre")) {
            if (operator.equals("plus")) {
                new PlusAssign(var, new IntegerConstExp(1)).codegen(cw, mv);
            } else if (operator.equals("minus")) {
                new MinusAssign(var, new IntegerConstExp(1)).codegen(cw, mv);
            }

            new SimpleVariable(var.getName(), var.getType()).codegen(cw, mv);
        }

    }

}
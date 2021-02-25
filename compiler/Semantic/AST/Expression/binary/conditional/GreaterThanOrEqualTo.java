package Semantic.AST.Expression.binary.conditional;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class GreaterThanOrEqualTo extends ConditionalExpression {
    public GreaterThanOrEqualTo(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        super.codegen(cw, mv);
    }

    @Override
    public int determineOp(Type type) {
        if (type == Type.DOUBLE_TYPE) {
            opCode = Opcodes.IFLT;
            compareCode = Opcodes.DCMPG;
        } else if (type == Type.FLOAT_TYPE) {
            opCode = Opcodes.IFLT;
            compareCode = Opcodes.FCMPG;
        } else if (type == Type.LONG_TYPE) {
            opCode = Opcodes.IFLT;
            compareCode = Opcodes.LCMP;
        } else if (type == Type.INT_TYPE)
            opCode = Opcodes.IF_ICMPLT;
        else
            System.out.println("Type Mismatch");
        return 0;
    }
}
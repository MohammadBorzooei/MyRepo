package Semantic.AST.Expression.unary;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import Semantic.AST.Type.CheckType;

public class Cast extends UnaryExpression {
    public Cast(Expression expression, Type castType) {
        super(expression);
        this.castType = castType;
    }

    public Type getCastType() {
        return castType;
    }

    public void setCastType(Type castType) {
        this.castType = castType;
    }

    private Type castType;

    @Override
    public Type getType() {
        return CheckType.unaryExprTypeCheck(castType);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        expression.codegen(cw, mv);
        Type from = expression.getType();
        if (from == castType)
            return;
        if (castType != Type.INT_TYPE && castType != Type.LONG_TYPE && castType != Type.DOUBLE_TYPE && castType != Type.FLOAT_TYPE)
            throw new RuntimeException("Wrong Cast.");
        mv.visitInsn(getOpcode(from, castType));
    }

    public static int getOpcode(Type from, Type to) {
        int opcode;
        if (from == Type.INT_TYPE) {
            if (to == Type.LONG_TYPE)
                opcode = Opcodes.I2L;
            else if (to == Type.FLOAT_TYPE)
                opcode = Opcodes.I2F;
            else
                opcode = Opcodes.I2D;
        } else if (from == Type.LONG_TYPE) {
            if (to == Type.INT_TYPE)
                opcode = Opcodes.L2I;
            else if (to == Type.DOUBLE_TYPE)
                opcode = Opcodes.L2D;
            else
                opcode = Opcodes.L2F;

        } else if (from == Type.DOUBLE_TYPE) {
            if (to == Type.INT_TYPE)
                opcode = Opcodes.D2I;
            else if (to == Type.LONG_TYPE)
                opcode = Opcodes.D2L;
            else
                opcode = Opcodes.D2F;
        } else {
            if (to == Type.LONG_TYPE)
                opcode = Opcodes.F2L;
            else if (to == Type.INT_TYPE)
                opcode = Opcodes.F2I;
            else
                opcode = Opcodes.F2D;
        }
        return opcode;
    }

    @Override
    public int determineOp(Type resultType) {
        return 0;
    }
}
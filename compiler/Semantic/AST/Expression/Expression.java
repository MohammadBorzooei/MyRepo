package Semantic.AST.Expression;

import Semantic.AST.AST;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class Expression implements AST {

    public abstract Type getType();

    public void castOperandType(Type resultType, MethodVisitor mv) {
        Type type = getType();
        if (type == resultType) {
            return;
        }
        if (type == Type.DOUBLE_TYPE) {
            if (resultType == Type.FLOAT_TYPE)
                mv.visitInsn(Opcodes.D2F);
            else if (resultType == Type.LONG_TYPE)
                mv.visitInsn(Opcodes.D2L);
            else if (resultType == Type.INT_TYPE)
                mv.visitInsn(Opcodes.D2I);
            else
                System.out.println("Type Mismatch");
        } else if (type == Type.FLOAT_TYPE) {
            if (resultType == Type.DOUBLE_TYPE)
                mv.visitInsn(Opcodes.F2D);
            else if (resultType == Type.LONG_TYPE)
                mv.visitInsn(Opcodes.F2L);
            else if (resultType == Type.INT_TYPE)
                mv.visitInsn(Opcodes.F2I);
            else
                System.out.println("Type Mismatch");
        } else if (type == Type.LONG_TYPE) {
            if (resultType == Type.DOUBLE_TYPE)
                mv.visitInsn(Opcodes.L2D);
            else if (resultType == Type.FLOAT_TYPE)
                mv.visitInsn(Opcodes.L2F);
            else if (resultType == Type.INT_TYPE)
                mv.visitInsn(Opcodes.L2I);
            else
                System.out.println("Type Mismatch");
        } else if (type == Type.INT_TYPE) {
            if (resultType == Type.DOUBLE_TYPE)
                mv.visitInsn(Opcodes.I2D);
            else if (resultType == Type.FLOAT_TYPE)
                mv.visitInsn(Opcodes.I2F);
            else if (resultType == Type.LONG_TYPE)
                mv.visitInsn(Opcodes.I2L);
            else
                System.out.println("Type Mismatch");
        } else
            System.out.println("Type Mismatch");
    }
}

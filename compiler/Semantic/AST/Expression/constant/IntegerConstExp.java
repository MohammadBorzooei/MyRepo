package Semantic.AST.Expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class IntegerConstExp extends Constant {
    private final Integer value;

    public IntegerConstExp(Integer value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.INT_TYPE;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        storeIntValue(mv, value);
    }

    public static void storeIntValue(MethodVisitor mv, Integer value) {
        if (value >= -1 && value <= 5) {
            switch (value) {
                case -1:
                    mv.visitInsn(ICONST_M1);
                    break;
                case 0:
                    mv.visitInsn(ICONST_0);
                    break;
                case 1:
                    mv.visitInsn(ICONST_1);
                    break;
                case 2:
                    mv.visitInsn(ICONST_2);
                    break;
                case 3:
                    mv.visitInsn(ICONST_3);
                    break;
                case 4:
                    mv.visitInsn(ICONST_4);
                    break;
                case 5:
                    mv.visitInsn(ICONST_5);
                    break;
            }
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }

    }
}
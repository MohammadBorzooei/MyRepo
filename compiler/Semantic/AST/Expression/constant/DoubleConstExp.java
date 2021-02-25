package Semantic.AST.Expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DCONST_1;

public class DoubleConstExp extends Constant {
    private final Double value;

    public DoubleConstExp(Double value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE_TYPE;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (value == 0)
            mv.visitInsn(DCONST_0);
        else if (value == 1)
            mv.visitInsn(DCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}
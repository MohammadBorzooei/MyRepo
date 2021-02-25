package Semantic.AST.Expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FCONST_1;

public class FloatConstExp extends Constant {

    private final Float value;

    public FloatConstExp(Float value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.FLOAT_TYPE;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (value == 0)
            mv.visitInsn(FCONST_0);
        else if (value == 1)
            mv.visitInsn(FCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}
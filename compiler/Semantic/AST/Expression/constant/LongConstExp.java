package Semantic.AST.Expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;

public class LongConstExp extends Constant {
    private final Long value;

    public LongConstExp(Long value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Type getType() {
        return Type.LONG_TYPE;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (value == 0)
            mv.visitInsn(LCONST_0);
        else if (value == 1)
            mv.visitInsn(LCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}
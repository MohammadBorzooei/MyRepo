package Semantic.AST.Expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StringConstExp extends Constant {
    private final String value;

    public StringConstExp(String value) {
        this.value = value.substring(1, value.length() - 1);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.getType(String.class);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitLdcInsn(value);
    }
}
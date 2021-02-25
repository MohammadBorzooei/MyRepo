package Semantic.AST.Expression;

import Semantic.AST.Operation;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class Input extends Expression implements Operation {
    Type type;

    public Input(Type t) {
        this.type = t;
    }

    @Override
    public Type getType() {
        return Type.INT_TYPE;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitTypeInsn(NEW, "java/util/Scanner");
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        if (this.type == null) {
            this.type = SymbolTable.getTypeFromStr("String");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Scanner", "nextLine", "()Ljava/lang/String;", false);
        } else {
            switch (this.type.getDescriptor()) {
                case "I":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()" + type.getDescriptor(), false);
                    break;
                case "J":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLong", "()" + type.getDescriptor(), false);
                    break;
                case "F":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextFloat", "()" + type.getDescriptor(), false);
                    break;
                case "D":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()" + type.getDescriptor(), false);
                    break;
                case "Z":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextBoolean", "()" + type.getDescriptor(), false);
                    break;
            }
        }
    }
}
package Semantic.AST.Expression.variable;

import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.SymbolTable.DSCP.DynamicLocalVariableDSCP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;

public class SimpleVariable extends Variable {
    public SimpleVariable(String name, Type type) {
        this.type = type;
        this.name = name;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        DSCP dscp = getDSCP();
        if (!dscp.isValid())
            throw new RuntimeException("Variable Does Not Have Any Initial Value.");
        if (dscp instanceof DynamicLocalVariableDSCP) {
            int index = ((DynamicLocalVariableDSCP) dscp).getIndex();
            mv.visitVarInsn(type.getOpcode(ILOAD), index);
        } else {
            mv.visitFieldInsn(GETSTATIC, "Main", name, type.getDescriptor());
        }
    }
}

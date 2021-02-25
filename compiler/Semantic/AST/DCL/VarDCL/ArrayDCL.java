package Semantic.AST.DCL.VarDCL;

import Semantic.AST.Expression.Expression;
import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.SymbolTable.DSCP.DynamicLocalArrayDSCP;
import Semantic.SymbolTable.DSCP.StaticGlobalArrayDSCP;
import Semantic.SymbolTable.SymbolTable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

public class ArrayDCL extends VarDCL {
    private ArrayList<Expression> dimensionsExpression;
    private final int dimensionNum;

    public ArrayDCL(String name, Type type, boolean global, int dimensionNum) {
        this.name = name;
        this.type = type;
        this.global = global;
        this.dimensionsExpression = new ArrayList<>(dimensionNum);
        this.dimensionNum = dimensionNum;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        for (Expression dim : this.dimensionsExpression) {
            dim.codegen(cw, mv);
        }
        StringBuilder arrayType = new StringBuilder();
        arrayType.append("[".repeat(Math.max(0, dimensionNum))).append(type.getDescriptor());

        if (this.global) {
            cw.visitField(ACC_STATIC, this.name, arrayType.toString(), null, null).visitEnd();
            if (this.dimensionNum == 1) {
                if (type.getDescriptor().startsWith("L"))
                    mv.visitTypeInsn(ANEWARRAY, this.type.getDescriptor());
                else
                    mv.visitIntInsn(NEWARRAY, SymbolTable.getTType(this.type));
            } else {
                mv.visitMultiANewArrayInsn(arrayType.toString(), dimensionsExpression.size());
            }
            mv.visitFieldInsn(PUTSTATIC, "Main", name, arrayType.toString());
        } else {
            if (this.dimensionNum == 1) {
                if (type.getDescriptor().startsWith("L"))
                    mv.visitTypeInsn(ANEWARRAY, this.type.getDescriptor());
                else
                    mv.visitIntInsn(NEWARRAY, SymbolTable.getTType(this.type));
            } else {
                mv.visitMultiANewArrayInsn(arrayType.toString(), dimensionsExpression.size());
            }
            mv.visitVarInsn(ASTORE, SymbolTable.getInstance().getIndex());
        }
        try {
            SymbolTable.getInstance().getDescriptor(name);
        } catch (Exception e) {
            declare(this.name, this.type, this.dimensionsExpression, this.dimensionNum, this.global);
        }
    }

    public static void declare(String name, Type type, ArrayList<Expression> dimensions, int dimNum, boolean global) {
        DSCP dscp;
        if (!global) {
            dscp = new DynamicLocalArrayDSCP(type, true, SymbolTable.getInstance().getIndex(), dimNum, dimensions);
        } else
            dscp = new StaticGlobalArrayDSCP(type, true, dimNum, dimensions);
        SymbolTable.getInstance().addVariable(name, dscp);
    }

    public void setDimensionsExpression(ArrayList<Expression> dimensionsExpression) {
        this.dimensionsExpression = dimensionsExpression;
    }
}

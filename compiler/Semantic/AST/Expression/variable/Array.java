package Semantic.AST.Expression.variable;

import Semantic.AST.Expression.Expression;
import Semantic.SymbolTable.DSCP.*;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;


public class Array extends Variable {
    private ArrayList<Expression> indexesExpression;

    public Array(String name, ArrayList<Expression> indexesExpression, Type type) {
        this.name = name;
        this.type = type;
        this.indexesExpression = indexesExpression;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        int dimNum = 0;
        if (!SymbolTable.getInstance().getDescriptor(this.name).getType().equals(Type.getType(String.class))) {
            if (getDSCP() instanceof DynamicLocalDSCP) {
                dimNum = ((DynamicLocalArrayDSCP) getDSCP()).getDimension();
                mv.visitVarInsn(ALOAD, ((DynamicLocalDSCP) getDSCP()).getIndex());
            } else if (getDSCP() instanceof StaticGlobalDSCP) {
                dimNum = ((StaticGlobalArrayDSCP) getDSCP()).getDimension();
                StringBuilder arrayType = new StringBuilder();
                arrayType.append("[".repeat(Math.max(0, ((StaticGlobalArrayDSCP) getDSCP()).getDimension()))).append(type.getDescriptor());
                mv.visitFieldInsn(GETSTATIC, "Main", this.name, arrayType.toString());
            }
            if (dimNum != (this.getIndexesExpression()).size())
                throw new RuntimeException("Dimension Number And Index Expressions Number Not Matching.");
            if (dimNum > 1) {
                for (int i = dimNum - 1; i > 0; i--) {
                    this.getIndexesExpression().get(i).codegen(cw, mv);
                    mv.visitInsn(AALOAD);
                }
            }
            indexesExpression.get(0).codegen(cw, mv);
            mv.visitInsn(this.type.getOpcode(IALOAD));
        } else {
            dimNum = 1;
            mv.visitVarInsn(ALOAD, ((DynamicLocalDSCP) getDSCP()).getIndex());
            if (indexesExpression.size() != dimNum) {
                throw new RuntimeException("String Has Only One Index.");
            }
            indexesExpression.get(0).codegen(cw, mv);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
            this.type = Type.CHAR_TYPE;
        }
    }

    public void setIndexesExpression(ArrayList<Expression> indexesExpression) {
        this.indexesExpression = indexesExpression;
    }

    public ArrayList<Expression> getIndexesExpression() {
        return indexesExpression;
    }
}

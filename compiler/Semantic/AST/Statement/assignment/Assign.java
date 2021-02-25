package Semantic.AST.Statement.assignment;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.variable.Array;
import Semantic.AST.Expression.variable.Variable;
import Semantic.SymbolTable.DSCP.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class Assign extends Assignment {
    public Assign(Variable variable, Expression expression) {
        super(variable, expression);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        checkIsConst();
        DSCP dscp = variable.getDSCP();
        if (dscp instanceof DynamicLocalDSCP) {
            int index = ((DynamicLocalDSCP) dscp).getIndex();
            if (dscp instanceof DynamicLocalVariableDSCP) {
                this.expression.getType();
                this.expression.codegen(cw, mv);
                this.expression.castOperandType(variable.getType(), mv);
                mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
            } else if (dscp instanceof DynamicLocalArrayDSCP) {
                mv.visitVarInsn(ALOAD, index);
                int dimNum = ((DynamicLocalArrayDSCP) (variable.getDSCP())).getDimension();
                doArrayStuff(cw, mv, dimNum);
                mv.visitInsn(variable.getType().getOpcode(IASTORE));
            }
        } else {
            if (dscp instanceof StaticGlobalVariableDSCP) {
                this.expression.getType();
                this.expression.codegen(cw, mv);
                this.expression.castOperandType(variable.getType(), mv);
                mv.visitFieldInsn(PUTSTATIC, "Main", variable.getName(), dscp.getType().toString());
            } else if (dscp instanceof StaticGlobalArrayDSCP) {
                StringBuilder arrayType = new StringBuilder();
                arrayType.append("[".repeat(Math.max(0, ((StaticGlobalArrayDSCP) dscp).getDimension()))).append(variable.getType().getDescriptor());
                mv.visitFieldInsn(GETSTATIC, "Main", this.variable.getName(), arrayType.toString());
                int dimNum = ((StaticGlobalArrayDSCP) (variable.getDSCP())).getDimension();
                doArrayStuff(cw, mv, dimNum);
                mv.visitInsn(IASTORE);
            }
        }
        dscp.setValid(true);
    }

    private void doArrayStuff(ClassWriter cw, MethodVisitor mv, int dimNum) {
        if (dimNum != (((Array) variable).getIndexesExpression()).size())
            throw new RuntimeException("Dimension Number And Index Expressions Number Not Matching.");
        if (dimNum > 1) {
            for (int i = dimNum - 1; i > 0; i--) {
                ((Array) variable).getIndexesExpression().get(i).codegen(cw, mv);
                mv.visitInsn(AALOAD);
            }
        }
        ((Array) variable).getIndexesExpression().get(0).codegen(cw, mv);
        this.expression.getType();
        this.expression.codegen(cw, mv);
        this.expression.castOperandType(variable.getType(), mv);
    }
}

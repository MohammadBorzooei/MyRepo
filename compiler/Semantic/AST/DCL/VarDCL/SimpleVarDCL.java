package Semantic.AST.DCL.VarDCL;

import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.variable.SimpleVariable;
import Semantic.AST.Expression.variable.Variable;
import Semantic.SymbolTable.DSCP.*;
import Semantic.SymbolTable.SymbolTable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;


public class SimpleVarDCL extends VarDCL {

    private boolean constant;
    private Expression expression;

    public SimpleVarDCL(String name, Type type, boolean constant, boolean global) {
        this.name = name;
        this.type = type;
        this.constant = constant;
        this.global = global;
    }

    public SimpleVarDCL(String name, String strType, boolean constant, boolean global, Expression expression) {
        this.name = name;
        this.constant = constant;
        this.global = global;
        this.expression = expression;
        if (!strType.equals("auto")) {
            this.type = SymbolTable.getTypeFromStr(strType);
        } else {
            if (expression == null) {
                throw new RuntimeException("There Is No Expression For Auto Variable.");
            } else {
                WTFMethodVisitor mv = new WTFMethodVisitor();
                WTFClassWriter cw = new WTFClassWriter();
                this.expression.codegen(cw, mv);
                this.type = this.expression.getType();
            }
        }
    }

    public void assign(Variable v, Expression e, ClassWriter cw, MethodVisitor mv) {
        DSCP dscp = v.getDSCP();
        e.getType();
        e.codegen(cw, mv);
        e.castOperandType(type, mv);
        if (dscp instanceof DynamicLocalDSCP) {
            int idx = ((DynamicLocalDSCP) dscp).getIndex();
            mv.visitVarInsn(v.getType().getOpcode(ISTORE), idx);
        } else {
            mv.visitFieldInsn(PUTSTATIC, "Main", v.getName(), dscp.getType().toString());
        }
        dscp.setValid(true);
    }

    public void declare() {
        DSCP dscp;
        if (this.global) {
            dscp = new StaticGlobalVariableDSCP(this.type, this.expression != null, this.constant);
        } else {
            dscp = new DynamicLocalVariableDSCP(this.type, this.expression != null,
                    SymbolTable.getInstance().getIndex(), this.constant);
        }
        SymbolTable.getInstance().addVariable(name, dscp);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        try {
            DSCP dscp = SymbolTable.getInstance().getDescriptor(name);
            if (dscp instanceof StaticGlobalDSCP && SymbolTable.getInstance().getScopesStack().size() > 1) {
                declare();
            }
        } catch (Exception e) {
            declare();
        }
        if (global) {
            int access = ACC_STATIC + (constant ? ACC_FINAL : 0);
            cw.visitField(access, this.name, this.type.getDescriptor(), null, null).visitEnd();
            if (expression != null) {
                assign(new SimpleVariable(this.name, this.type), this.expression, cw, mv);
            }

        } else if (expression != null) {
            this.expression.getType();
            this.expression.codegen(cw, mv);
            this.expression.castOperandType(type, mv);
            DynamicLocalVariableDSCP dscp = (DynamicLocalVariableDSCP) SymbolTable.getInstance().getDescriptor(name);
            mv.visitVarInsn(type.getOpcode(ISTORE), dscp.getIndex());
        }
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
        SymbolTable.getInstance().getDescriptor(name).setValid(true);
    }
}

class WTFMethodVisitor extends MethodVisitor {
    public WTFMethodVisitor() {
        super(327680);
    }
}

class WTFClassWriter extends ClassWriter {

    public WTFClassWriter() {
        super(327680);
    }
}

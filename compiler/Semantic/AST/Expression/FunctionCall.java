package Semantic.AST.Expression;

import Semantic.AST.DCL.FunctionDCL;
import Semantic.AST.Expression.variable.Array;
import Semantic.AST.Operation;
import Semantic.SymbolTable.DSCP.*;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

public class FunctionCall extends Expression implements Operation {
    String name;
    ArrayList<Expression> arguments;
    ArrayList<Type> argumentTypes;
    FunctionDCL functionDCL;

    public FunctionCall(String name, ArrayList<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
        this.argumentTypes = new ArrayList<>();
    }

    public void addArgument(Expression e) {
        if (this.arguments == null) {
            this.arguments = new ArrayList<>();
        }
        this.arguments.add(e);
    }

    @Override
    public Type getType() {
        if (this.argumentTypes.size() == 0)
            for (Expression e : this.arguments) {
                if (!(e instanceof Array))
                    this.argumentTypes.add(e.getType());
                else {
                    StringBuilder numOfDim = new StringBuilder();
                    numOfDim.append("[".repeat(Math.max(0, ((Array) e).getIndexesExpression().size())));
                    this.argumentTypes.add(Type.getType(numOfDim.toString() + (e).getType()));
                }
            }
        this.functionDCL = SymbolTable.getInstance().getFunction(this.name, this.argumentTypes);
        if (this.functionDCL == null) {
            throw new RuntimeException("No Such Function Found");
        }
        return this.functionDCL.getType();
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        this.argumentTypes = new ArrayList<>();
        for (Expression e : this.arguments) {
            if (!(e instanceof Array)) {
                this.argumentTypes.add(e.getType());
                e.codegen(cw, mv);
            } else {
                int dimNum = 0;
                DSCP dscp = ((Array) e).getDSCP();
                if (dscp instanceof DynamicLocalDSCP) {
                    dimNum = ((DynamicLocalArrayDSCP) dscp).getDimension();
                } else if (dscp instanceof StaticGlobalDSCP) {
                    dimNum = ((StaticGlobalArrayDSCP) dscp).getDimension();
                }
                StringBuilder numOfDim = new StringBuilder();
                numOfDim.append("[".repeat(Math.max(0, dimNum)));
                Type t = Type.getType(numOfDim.toString() + (e).getType());
                this.argumentTypes.add(t);
                if (dscp instanceof DynamicLocalDSCP) {
                    mv.visitVarInsn(ALOAD, ((DynamicLocalDSCP) dscp).getIndex());
                } else if (dscp instanceof StaticGlobalDSCP) {
                    mv.visitFieldInsn(GETSTATIC, "Main", ((Array) e).getName(), t.toString());
                }
            }
        }
        this.functionDCL = SymbolTable.getInstance().getFunction(this.name, this.argumentTypes);
        if (this.functionDCL == null) {
            throw new RuntimeException("No Such Function Found");
        }
        if (this.arguments.size() != this.functionDCL.getInputArguments().size())
            throw new IllegalArgumentException("Number Of Input Arguments Does Not Match.");
        mv.visitMethodInsn(INVOKESTATIC, "Main", this.name, this.functionDCL.getSignature(), false);
    }
}
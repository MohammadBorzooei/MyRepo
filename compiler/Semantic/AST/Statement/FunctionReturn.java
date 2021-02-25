package Semantic.AST.Statement;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import Semantic.AST.DCL.FunctionDCL;
import Semantic.AST.Expression.Expression;
import Semantic.SymbolTable.Frame;
import Semantic.SymbolTable.SymbolTable;

import static org.objectweb.asm.Opcodes.*;

public class FunctionReturn extends Statement {

    private final Expression expression;
    private Frame scope;

    public FunctionReturn(Expression expression, FunctionDCL functionDCL) {
        this.expression = expression;
        if ((expression == null && !functionDCL.getType().equals(Type.VOID_TYPE)) ||
                (expression != null && (functionDCL.getType().equals(Type.VOID_TYPE))))
            throw new RuntimeException("Return Types Mismatching.");
        functionDCL.addReturn(this);
    }


    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        FunctionDCL functionDCL = SymbolTable.getInstance().getLastFunction();
        scope = SymbolTable.getInstance().getLastScope();
        int index = functionDCL.getReturns().indexOf(this);
        for (int i = 0; i < index; i++) {
            FunctionReturn funcReturn = functionDCL.getReturns().get(i);
            if (funcReturn.scope.equals(scope)) {
                throw new RuntimeException("More Than One Return In Single Scope.");
            }
        }
        if (expression == null) {
            mv.visitInsn(RETURN);
        } else {
            expression.getType();
            expression.codegen(cw, mv);
            expression.castOperandType(functionDCL.getType(), mv);
            mv.visitInsn(functionDCL.getType().getOpcode(IRETURN));

        }
    }
}

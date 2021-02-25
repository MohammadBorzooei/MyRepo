package Semantic.AST.Statement;

import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.Frame;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Break extends Statement {

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (SymbolTable.getInstance().canHaveBreak()) {
            int i = SymbolTable.getInstance().getScopesStack().size() - 1;
            for (; i >= 0; i--) {
                Frame scope = SymbolTable.getInstance().getScopesStack().get(i);
                if (scope.getScopeType() == Scope.LOOP) {
                    mv.visitJumpInsn(GOTO, SymbolTable.getInstance().getInnerLoop().getEnd());
                    return;
                } else if (scope.getScopeType() == Scope.SWITCH) {
                    mv.visitJumpInsn(GOTO, SymbolTable.getInstance().getLastSwitch().getEnd());
                    return;
                }
            }
        } else
            throw new RuntimeException("Break Not In Switch Or Loop.");
    }
}

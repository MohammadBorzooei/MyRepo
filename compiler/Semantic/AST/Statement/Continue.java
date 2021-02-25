package Semantic.AST.Statement;

import Semantic.AST.Statement.Statement;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Continue extends Statement {

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (SymbolTable.getInstance().getInnerLoop() != null)
            mv.visitJumpInsn(GOTO, SymbolTable.getInstance().getInnerLoop().getStartLoop());
        else
            throw new RuntimeException("Continue Not In Switch Or Loop.");
    }
}

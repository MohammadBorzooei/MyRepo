package Semantic.AST.Statement.condition;

import Semantic.AST.Block.Block;
import Semantic.AST.Expression.constant.IntegerConstExp;
import Semantic.AST.Statement.Statement;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Case extends Statement {
    IntegerConstExp integerConstExp;
    private final Block block;
    Label StartCase = new Label();
    Label jump;

    public Case(IntegerConstExp integerConstExp, Block block) {
        this.integerConstExp = integerConstExp;
        this.block = block;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitLabel(StartCase);
        SymbolTable.getInstance().addScope(Scope.SWITCH);
        block.codegen(cw, mv);
        SymbolTable.getInstance().popScope();
        mv.visitJumpInsn(GOTO, jump);
    }
}

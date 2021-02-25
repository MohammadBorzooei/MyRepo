package Semantic.AST.Statement.Loop;

import Semantic.AST.Block.Block;
import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.binary.conditional.NotEqual;
import Semantic.AST.Expression.constant.IntegerConstExp;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFNE;

public class Repeat extends Loop {
    private final Expression expression;

    public Repeat(Block block, Expression expression) {
        super(block);
        this.expression = expression;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        SymbolTable.getInstance().addScope(Scope.LOOP);
        SymbolTable.getInstance().setInnerLoop(this);
        mv.visitLabel(startLoop);
        block.codegen(cw, mv);
        NotEqual notEqual = new NotEqual(expression, new IntegerConstExp(0));
        notEqual.codegen(cw, mv);
        mv.visitJumpInsn(IFNE, startLoop);
        mv.visitLabel(end);
        SymbolTable.getInstance().popScope();
        SymbolTable.getInstance().setInnerLoop(null);
    }
}

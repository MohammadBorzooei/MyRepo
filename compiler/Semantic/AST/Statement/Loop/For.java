package Semantic.AST.Statement.Loop;

import Semantic.AST.Block.Block;
import Semantic.AST.Expression.Expression;
import Semantic.AST.Expression.InitialExpression;
import Semantic.AST.Expression.StepExpression;
import Semantic.AST.Expression.binary.conditional.NotEqual;
import Semantic.AST.Expression.constant.IntegerConstExp;
import Semantic.AST.Expression.unary.PostMinusMinus;
import Semantic.AST.Expression.unary.PostPlusPlus;
import Semantic.AST.Expression.unary.PreMinusMinus;
import Semantic.AST.Expression.unary.PrePlusPlus;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class For extends Loop {

    private final InitialExpression init;
    private final Expression expression;
    private final StepExpression step;
    private final Label expLabel = new Label();
    private final Label stepLabel = new Label();
    private final Label blockLabel = new Label();


    public For(Block block, InitialExpression init, Expression expression, StepExpression step) {
        super(block);
        this.init = init;
        this.expression = expression;
        this.step = step;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        SymbolTable.getInstance().addScope(Scope.LOOP);
        SymbolTable.getInstance().setInnerLoop(this);
        if (init != null) {
            init.codegen(cw, mv);
            if (init instanceof PostPlusPlus || init instanceof PrePlusPlus
                    || init instanceof PostMinusMinus || init instanceof PreMinusMinus)
                mv.visitInsn(POP);
        }
        mv.visitLabel(expLabel);

        NotEqual notEqual = new NotEqual(expression, new IntegerConstExp(0));
        notEqual.codegen(cw, mv);
        mv.visitJumpInsn(IFEQ, end);
        mv.visitJumpInsn(GOTO, blockLabel);

        mv.visitLabel(stepLabel);
        mv.visitLabel(startLoop);
        if (step != null) {
            step.codegen(cw, mv);
            if (step instanceof PostPlusPlus || step instanceof PrePlusPlus
                    || step instanceof PostMinusMinus || step instanceof PreMinusMinus)
                mv.visitInsn(POP);
        }

        mv.visitJumpInsn(GOTO, expLabel);

        mv.visitLabel(blockLabel);
        block.codegen(cw, mv);
        mv.visitJumpInsn(GOTO, stepLabel);

        mv.visitLabel(end);

        SymbolTable.getInstance().popScope();
        SymbolTable.getInstance().setInnerLoop(null);
    }
}

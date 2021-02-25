package Semantic.AST.Statement.condition;

import Semantic.AST.Block.Block;
import Semantic.AST.Expression.Expression;
import Semantic.AST.Statement.Statement;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.GOTO;

public class Switch extends Statement {
    private Expression expression;
    private ArrayList<Case> cases;
    private Block defaultBlock;
    private Label defaultLabel = new Label();
    private Label lookUpTable = new Label();
    private Label end = new Label();


    public Switch(Expression expression, ArrayList<Case> cases, Block defaultBlock) {
        this.expression = expression;
        this.cases = cases;
        this.defaultBlock = defaultBlock;
    }

    public void addCase(Case caseSt) {
        if (cases == null)
            cases = new ArrayList<>();
        cases.add(caseSt);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        SymbolTable.getInstance().addScope(Scope.SWITCH);
        SymbolTable.getInstance().setLastSwitch(this);
        Label[] labels = new Label[cases.size()];
        int[] keys = new int[cases.size()];
        int i = 0;
        expression.codegen(cw, mv);
        mv.visitJumpInsn(GOTO, lookUpTable);
        for (Case c : cases) {
            c.jump = end;
            c.codegen(cw, mv);
            labels[i] = c.StartCase;
            keys[i++] = (int) c.integerConstExp.getValue();
        }
        mv.visitLabel(defaultLabel);
        if (defaultBlock != null) {
            SymbolTable.getInstance().addScope(Scope.SWITCH);
            defaultBlock.codegen(cw, mv);
            SymbolTable.getInstance().popScope();
        }
        mv.visitJumpInsn(GOTO, end);
        mv.visitLabel(lookUpTable);
        mv.visitLookupSwitchInsn(defaultLabel, keys, labels);
        mv.visitLabel(end);
        SymbolTable.getInstance().popScope();
        SymbolTable.getInstance().setLastSwitch(null);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

    public void setCases(ArrayList<Case> cases) {
        this.cases = cases;
    }

    public Block getDefaultBlock() {
        return defaultBlock;
    }

    public void setDefaultBlock(Block defaultBlock) {
        this.defaultBlock = defaultBlock;
    }

    public Label getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(Label defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    public Label getLookUpTable() {
        return lookUpTable;
    }

    public void setLookUpTable(Label lookUpTable) {
        this.lookUpTable = lookUpTable;
    }

    public Label getEnd() {
        return end;
    }

    public void setEnd(Label end) {
        this.end = end;
    }
}

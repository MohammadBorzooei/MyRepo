package Semantic.AST.Statement.Loop;

import Semantic.AST.Block.Block;
import Semantic.AST.Statement.Statement;
import org.objectweb.asm.Label;

public abstract class Loop extends Statement {
    protected Block block;
    Label startLoop = new Label();
    Label end = new Label();

    Loop(Block block) {
        this.block = block;
    }

    public Label getStartLoop() {
        return startLoop;
    }

    public void setStartLoop(Label startLoop) {
        this.startLoop = startLoop;
    }

    public Label getEnd() {
        return end;
    }

    public void setEnd(Label end) {
        this.end = end;
    }
}

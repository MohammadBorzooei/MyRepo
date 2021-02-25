package Semantic.SymbolTable.DSCP;

import org.objectweb.asm.Type;

public abstract class DynamicLocalDSCP extends DSCP {
    int index;

    public DynamicLocalDSCP(Type type, boolean isValid, int index) {
        super(type, isValid);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

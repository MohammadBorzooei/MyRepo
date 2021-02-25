package Semantic.SymbolTable.DSCP;

import org.objectweb.asm.Type;

public abstract class DSCP {
    boolean isValid;
    Type type;
    boolean constant;

    public DSCP(Type type, boolean isValid) {
        this.type = type;
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }
}

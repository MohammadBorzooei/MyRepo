package Semantic.SymbolTable.DSCP;

import org.objectweb.asm.Type;

public class DynamicLocalVariableDSCP extends DynamicLocalDSCP {
    public DynamicLocalVariableDSCP(Type type, boolean isValid, int index, boolean constant) {
        super(type, isValid, index);
        this.constant = constant;
    }
}

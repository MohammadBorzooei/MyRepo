package Semantic.SymbolTable.DSCP;

import org.objectweb.asm.Type;

public abstract class StaticGlobalDSCP extends DSCP {
    public StaticGlobalDSCP(Type type, boolean isValid) {
        super(type, isValid);
    }
}

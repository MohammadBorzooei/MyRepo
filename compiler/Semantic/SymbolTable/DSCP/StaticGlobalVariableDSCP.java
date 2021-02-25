package Semantic.SymbolTable.DSCP;

import org.objectweb.asm.Type;

public class StaticGlobalVariableDSCP extends StaticGlobalDSCP {
    public StaticGlobalVariableDSCP(Type type, boolean isValid, boolean constant) {
        super(type, isValid);
        this.constant = constant;
    }
}

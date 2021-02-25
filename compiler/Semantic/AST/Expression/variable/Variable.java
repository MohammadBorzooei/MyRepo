package Semantic.AST.Expression.variable;

import Semantic.AST.Expression.Expression;
import Semantic.SymbolTable.SymbolTable;
import Semantic.SymbolTable.DSCP.DSCP;
import org.objectweb.asm.Type;

public abstract class Variable extends Expression {
    public String name;
    public Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type getType() {
        return getDSCP().getType();
    }

    public DSCP getDSCP() {
        return SymbolTable.getInstance().getDescriptor(name);
    }
}
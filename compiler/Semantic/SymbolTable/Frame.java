package Semantic.SymbolTable;

import Semantic.SymbolTable.DSCP.DSCP;

import java.util.HashMap;

public class Frame extends HashMap<String, DSCP> {

    private int index = 0;
    private Scope scopeType;

    public void addIndex(int add) {
        index += add;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Scope getScopeType() {
        return scopeType;
    }

    public void setScopeType(Scope scopeType) {
        this.scopeType = scopeType;
    }
}
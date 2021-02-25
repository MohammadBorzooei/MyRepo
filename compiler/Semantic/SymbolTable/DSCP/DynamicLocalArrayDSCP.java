package Semantic.SymbolTable.DSCP;

import Semantic.AST.Expression.Expression;
import org.objectweb.asm.Type;

import java.util.ArrayList;

public class DynamicLocalArrayDSCP extends DynamicLocalDSCP {

    int dimension;

    public int getDimension() {
        return dimension;
    }

    public void setDimensionList(ArrayList<Expression> dimensionList) {
        this.dimensionList = dimensionList;
    }

    ArrayList<Expression> dimensionList;

    public DynamicLocalArrayDSCP(Type type, boolean isValid, int index, int dimension, ArrayList<Expression> dimensionList) {
        super(type, isValid, index);
        this.dimension = dimension;
        this.dimensionList = dimensionList;
    }
}

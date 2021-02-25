package Semantic.AST.Type;

import org.objectweb.asm.Type;

public class CheckType {
    public static Type unaryExprTypeCheck(Type t) {
        if (t.equals(Type.getType(String.class)))
            return Type.getType(String.class);
        if (t.equals(Type.CHAR_TYPE))
            return t;
        if (t == Type.BOOLEAN_TYPE)
            return t;
        if (!isValidExprType(t))
            System.out.println("Type mismatch");
        if (t == Type.DOUBLE_TYPE)
            return Type.DOUBLE_TYPE;
        else if (t == Type.FLOAT_TYPE)
            return Type.FLOAT_TYPE;
        else if (t == Type.LONG_TYPE)
            return Type.LONG_TYPE;
        else
            return Type.INT_TYPE;
    }

    public static Type binaryExprTypeCheck(Type t1, Type t2) {
        if (t1.equals(Type.getType(String.class)) && t2.equals(Type.getType(String.class)))
            return Type.getType(String.class);
        if (t1 == Type.CHAR_TYPE && t2 == Type.CHAR_TYPE) {
            return Type.CHAR_TYPE;
        }
        if (!(isValidExprType(t1) && isValidExprType(t2)))
            System.out.println("Type mismatch");
        if (t1 == Type.DOUBLE_TYPE || t2 == Type.DOUBLE_TYPE)
            return Type.DOUBLE_TYPE;
        else if (t1 == Type.FLOAT_TYPE || t2 == Type.FLOAT_TYPE)
            return Type.FLOAT_TYPE;
        else if (t1 == Type.LONG_TYPE || t2 == Type.LONG_TYPE)
            return Type.LONG_TYPE;
        else
            return Type.INT_TYPE;
    }

    public static boolean isValidExprType(Type type) {
        return type == Type.BOOLEAN_TYPE || type == Type.CHAR_TYPE || type == Type.INT_TYPE || type == Type.LONG_TYPE || type == Type.FLOAT_TYPE || type == Type.DOUBLE_TYPE;
    }
}
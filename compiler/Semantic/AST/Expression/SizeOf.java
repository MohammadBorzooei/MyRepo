package Semantic.AST.Expression;

import Semantic.AST.Expression.constant.IntegerConstExp;
import Semantic.AST.Operation;
import Semantic.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class SizeOf extends Expression implements Operation {
    Integer value;

    public SizeOf(String id) {
        Type t = SymbolTable.getInstance().getDescriptor(id).getType();
        if (!t.toString().endsWith(";"))
            value = SymbolTable.getSize(t.getClassName());
        else
            throw new RuntimeException("Sizeof Is Used For a Variable Without Base Type.");
    }

    @Override
    public Type getType() {
        return Type.INT_TYPE;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        IntegerConstExp.storeIntValue(mv, value);
    }
}
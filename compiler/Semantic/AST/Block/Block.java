package Semantic.AST.Block;


import Semantic.AST.AST;
import Semantic.AST.Operation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class Block implements AST {

    private final ArrayList<Operation> operations;

    public Block(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (operations == null)
            throw new RuntimeException("This Block Has No Expression.");
        for (Operation op : operations) {
            op.codegen(cw, mv);
        }
    }
}

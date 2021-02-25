package Semantic.AST.Block;

import Semantic.AST.AST;
import Semantic.AST.DCL.Declaration;
import Semantic.AST.Expression.FunctionCall;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;


import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class GlobalBlock implements AST {

    private final ArrayList<AST> declarations;

    private static GlobalBlock instance = new GlobalBlock();

    private GlobalBlock() {
        this.declarations = new ArrayList<>();
    }

    public static void setInstance(GlobalBlock instance) {
        GlobalBlock.instance = instance;
    }

    public static GlobalBlock getInstance() {
        return instance;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    public ArrayList<AST> getDeclarations() {
        return declarations;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        new FunctionCall("start", new ArrayList<>()).codegen(cw, mv);
        mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();

        for (AST declaration : declarations) {
            declaration.codegen(cw, mv);
        }

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}

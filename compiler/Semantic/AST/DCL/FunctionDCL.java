package Semantic.AST.DCL;

import Semantic.AST.Block.Block;
import Semantic.AST.Statement.FunctionReturn;
import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.SymbolTable.DSCP.DynamicLocalArrayDSCP;
import Semantic.SymbolTable.DSCP.DynamicLocalDSCP;
import Semantic.SymbolTable.DSCP.DynamicLocalVariableDSCP;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

import java.util.*;

public class FunctionDCL implements Declaration {
    private Type type;
    private String name;
    private String signature;
    private final HashMap<String, DSCP> inputArguments;
    private final ArrayList<Type> argumentTypes = new ArrayList<>();
    private ArrayList<FunctionReturn> returns = new ArrayList<>();
    private Block block;
    private Boolean signatureDeclared = false;


    public void addReturn(FunctionReturn inputReturn) {
        returns.add(inputReturn);
    }

    public FunctionDCL(Type type, String name, HashMap<String, DSCP> inputArguments, Block block) {
        this.type = type;
        this.name = name;
        this.inputArguments = inputArguments;
        this.block = block;

        if (name.equals("start")) {
            this.signature = "()V";
        }
    }

    public void setSignature() {
        StringBuilder signature = new StringBuilder("(");
        for (Type t : argumentTypes) {
            signature.append(t.toString());
        }
        signature.append(")");
        signature.append(type.toString());
        this.signature = signature.toString();
    }

    public void addArgument(String name, DynamicLocalDSCP dscp) {
        inputArguments.put(name, dscp);
        if (dscp instanceof DynamicLocalVariableDSCP)
            argumentTypes.add(dscp.getType());
        else if (dscp instanceof DynamicLocalArrayDSCP) {
            StringBuilder numOfDim = new StringBuilder();
            numOfDim.append("[".repeat(Math.max(0, ((DynamicLocalArrayDSCP) dscp).getDimension())));
            argumentTypes.add(Type.getType(numOfDim.toString() + dscp.getType()));
        }
    }

    public void declare() {
        SymbolTable.getInstance().addFunction(this);
    }

    public boolean checkEqual(String name, List<Type> argumentTypes) {
        if (this.name.equals(name)) {
            if (argumentTypes.size() == this.argumentTypes.size()) {
                for (int i = 0; i < argumentTypes.size(); i++) {
                    if (!argumentTypes.get(i).equals(this.argumentTypes.get(i)))
                        return false;
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        setSignature();
        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, this.name, this.signature,
                null, null);
        SymbolTable.getInstance().addScope(Scope.FUNCTION);
        inputArguments.forEach((s, dscp) -> SymbolTable.getInstance().addVariable(s, dscp));
        SymbolTable.getInstance().setLastFunction(this);
        methodVisitor.visitCode();
        this.block.codegen(cw, methodVisitor);
        if (returns.size() == 0) {
            throw new RuntimeException("There Is No Return Statement.");
        }
        methodVisitor.visitEnd();
        SymbolTable.getInstance().popScope();
        SymbolTable.getInstance().setLastFunction(null);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public HashMap<String, DSCP> getInputArguments() {
        return inputArguments;
    }

    public ArrayList<Type> getArgumentTypes() {
        return argumentTypes;
    }

    public ArrayList<FunctionReturn> getReturns() {
        return returns;
    }

    public void setReturns(ArrayList<FunctionReturn> returns) {
        this.returns = returns;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Boolean getSignatureDeclared() {
        return signatureDeclared;
    }

    public void setSignatureDeclared(Boolean signatureDeclared) {
        this.signatureDeclared = signatureDeclared;
    }
}

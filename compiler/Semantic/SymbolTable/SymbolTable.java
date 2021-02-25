package Semantic.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import Semantic.SymbolTable.DSCP.DSCP;
import Semantic.AST.DCL.FunctionDCL;
import Semantic.AST.DCL.RecordDCL;
import Semantic.AST.Statement.condition.Switch;
import Semantic.AST.Statement.Loop.Loop;
import Semantic.SymbolTable.DSCP.DynamicLocalDSCP;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SymbolTable {

    private static final SymbolTable instance = new SymbolTable();
    private FunctionDCL LastFunction;
    private Loop innerLoop;
    private Switch lastSwitch;
    private final ArrayList<Frame> scopesStack = new ArrayList<>();
    private final HashMap<String, ArrayList<FunctionDCL>> funcDCLs = new HashMap<>();
    private final HashMap<String, RecordDCL> recordDCLs = new HashMap<>();
    int numForTempId;

    private SymbolTable() {
        Frame mainFrame = new Frame();
        mainFrame.setIndex(1);
        mainFrame.setScopeType(Scope.GLOBAL);
        scopesStack.add(mainFrame);
    }

    public static SymbolTable getInstance() {
        return instance;
    }

    public static int getSize(String name) {
        switch (name) {
            case "string":
            case "int":
                return Integer.SIZE;
            case "long":
                return Long.SIZE;
            case "float":
                return Float.SIZE;
            case "double":
                return Double.SIZE;
            case "char":
                return Character.SIZE;
            case "bool":
                return 1;
            default:
                throw new IllegalArgumentException("Undefined Type.");
        }
    }

    public static Type getTypeFromStr(String str) {
        switch (str) {
            case "Integer":
            case "int":
            case "I":
                return Type.INT_TYPE;
            case "Long":
            case "long":
            case "J":
                return Type.LONG_TYPE;
            case "Float":
            case "float":
            case "F":
                return Type.FLOAT_TYPE;
            case "Double":
            case "double":
            case "D":
                return Type.DOUBLE_TYPE;
            case "Character":
            case "char":
            case "C":
                return Type.CHAR_TYPE;
            case "String":
            case "string":
            case "Ljava/lang/String;":
                return Type.getType(String.class);
            case "Boolean":
            case "bool":
            case "Z":
                return Type.BOOLEAN_TYPE;
            case "void":
            case "V":
                return Type.VOID_TYPE;
            default:
                return Type.getType("L" + str + ";");
        }
    }

    public static int getTType(Type type) {
        if (type == Type.INT_TYPE)
            return Opcodes.T_INT;
        else if (type == Type.LONG_TYPE)
            return Opcodes.T_LONG;
        else if (type == Type.FLOAT_TYPE)
            return Opcodes.T_FLOAT;
        else if (type == Type.DOUBLE_TYPE)
            return Opcodes.T_DOUBLE;
        else if (type == Type.CHAR_TYPE)
            return Opcodes.T_CHAR;
        else if (type == Type.BOOLEAN_TYPE)
            return Opcodes.T_BOOLEAN;
        else
            throw new RuntimeException(type + " Is Not Correct.");
    }

    public Set<String> getFuncNames() {
        return funcDCLs.keySet();
    }

    public void popScope() {
        scopesStack.remove(scopesStack.size() - 1);
    }

    public void addScope(Scope scopeType) {
        Frame frame = new Frame();
        frame.setScopeType(scopeType);
        if (scopeType != Scope.FUNCTION)
            frame.setIndex(getLastScope().getIndex());
        scopesStack.add(frame);
    }

    public Frame getLastScope() {
        if (scopesStack.size() == 0)
            throw new RuntimeException("Scopes Stacks Is Empty.");
        return scopesStack.get(scopesStack.size() - 1);
    }

    public void addFunction(FunctionDCL funcDCL) {
        if (!funcDCLs.containsKey(funcDCL.getName())) {
            ArrayList<FunctionDCL> funcDclList = new ArrayList<>();
            funcDclList.add(funcDCL);
            funcDCLs.put(funcDCL.getName(), funcDclList);
        } else {
            funcDCLs.get(funcDCL.getName()).add(funcDCL);
        }
    }

    public FunctionDCL getFunction(String name, ArrayList<Type> inputs) {
        if (funcDCLs.containsKey(name)) {
            ArrayList<FunctionDCL> funcDclMapper = funcDCLs.get(name);
            for (FunctionDCL f : funcDclMapper) {
                if (f.checkEqual(name, inputs)) {
                    return f;
                }
            }
        }
        return null;
    }

    public void addVariable(String name, DSCP dscp) {
        if (getLastScope().containsKey(name)) {
            throw new RuntimeException("Variable " + name + " Was Declared Previously.");
        }
        if (getLastScope().getScopeType() == Scope.CONDITION || getLastScope().getScopeType() == Scope.SWITCH || getLastScope().getScopeType() == Scope.LOOP) {
            int stackIndex = scopesStack.size();
            while (stackIndex > 0) {
                stackIndex--;
                Frame frame = scopesStack.get(stackIndex);
                if (frame.containsKey(name) && frame.getScopeType() != Scope.GLOBAL)
                    throw new RuntimeException("Variable " + name + " Was Declared Previously.");
            }
        }
        if (dscp instanceof DynamicLocalDSCP) {
            getLastScope().put(name, dscp);
            getLastScope().addIndex(1);
        } else
            scopesStack.get(0).put(name, dscp);
    }

    public FunctionDCL getLastFunction() {
        return LastFunction;
    }

    public void setLastFunction(FunctionDCL lastFunction) {
        LastFunction = lastFunction;
    }

    public DSCP getDescriptor(String name) {
        int stackIndex = scopesStack.size();
        while (stackIndex > 0) {
            stackIndex--;
            if (scopesStack.get(stackIndex).containsKey(name))
                return scopesStack.get(stackIndex).get(name);
        }
        throw new RuntimeException(name + " Has Not Been Initialized.");
    }

    public boolean canHaveBreak() {
        return getLastScope().getScopeType() == Scope.LOOP || getLastScope().getScopeType() == Scope.SWITCH;
    }
    public String getTempId() {
        String tempName = "temp$" + numForTempId;
        numForTempId++;
        return tempName;
    }

    public int getIndex() {
        return getLastScope().getIndex();
    }

    public ArrayList<Frame> getScopesStack() {
        return scopesStack;
    }

    public Loop getInnerLoop() {
        return innerLoop;
    }

    public void setInnerLoop(Loop innerLoop) {
        this.innerLoop = innerLoop;
    }

    public Switch getLastSwitch() {
        return lastSwitch;
    }

    public void setLastSwitch(Switch lastSwitch) {
        this.lastSwitch = lastSwitch;
    }

}
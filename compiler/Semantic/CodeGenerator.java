package Semantic;

import Semantic.AST.Block.Block;
import Semantic.AST.DCL.Declaration;
import Semantic.AST.DCL.FunctionDCL;
import Semantic.AST.DCL.VarDCL.ArrayDCL;
import Semantic.AST.DCL.VarDCL.SimpleVarDCL;
import Semantic.AST.DCL.VarDCL.VarDCL;
import Semantic.AST.Expression.*;
import Semantic.AST.Expression.binary.arithmetic.*;
import Semantic.AST.Expression.binary.conditional.*;
import Semantic.AST.Expression.constant.*;
import Semantic.AST.Expression.unary.*;
import Semantic.AST.Expression.variable.Array;
import Semantic.AST.Expression.variable.Record;
import Semantic.AST.Expression.variable.SimpleVariable;
import Semantic.AST.Expression.variable.Variable;
import Semantic.AST.Operation;
import Semantic.AST.Statement.Break;
import Semantic.AST.Statement.Continue;
import Semantic.AST.Statement.FunctionReturn;
import Semantic.AST.Statement.Loop.For;
import Semantic.AST.Statement.Loop.Repeat;
import Semantic.AST.Statement.Println;
import Semantic.AST.Statement.assignment.*;
import Semantic.AST.Statement.condition.Case;
import Semantic.AST.Statement.condition.If;
import Semantic.AST.Statement.condition.Switch;
import Semantic.SymbolTable.DSCP.*;
import Semantic.SymbolTable.Scope;
import Semantic.SymbolTable.SymbolTable;
import Syntax.Lexical;
import Semantic.AST.AST;

import java.util.*;

import Semantic.AST.Block.GlobalBlock;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class CodeGenerator implements Syntax.CodeGenerator {
    private final Lexical lexical;
    private final Deque<Object> semanticStack;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new ArrayDeque<>();
        semanticStack.push(GlobalBlock.getInstance());
    }

    public AST getResult() {
        return (AST) semanticStack.getFirst();
    }


    String lastSeenType = "";
    Byte lastSeenFlag;

    @Override
    public void doSemantic(String sem) {
        switch (sem) {
            case "push": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "pop": {
                semanticStack.pop();
                break;
            }
            case "createFlag": {
                Byte flag = 0;
                if (semanticStack.peek() instanceof SimpleVariable) {
                    SimpleVariable popped = (SimpleVariable) semanticStack.pop();
                    if (popped.getType().equals(Type.getType(String.class)))
                        semanticStack.push(new Array(popped.getName(), new ArrayList<>(), Type.CHAR_TYPE));
                    else
                        semanticStack.push(popped);
                }
                semanticStack.push(flag);
                break;
            }
            case "createBlock": {
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }
            case "makeFunctionDCL": {
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                FunctionDCL functionDCL = new FunctionDCL(type, (String) lexical.currentToken().getValue(), new HashMap<>(), null);
                semanticStack.push(functionDCL);
                SymbolTable.getInstance().setLastFunction(functionDCL);
                SymbolTable.getInstance().addScope(Scope.FUNCTION);
                break;
            }
            case "addArgument": {
                String name = ((NOP) semanticStack.pop()).name;
                DynamicLocalDSCP dscp = (DynamicLocalDSCP) SymbolTable.getInstance().getDescriptor(name);
                dscp.setValid(true);
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.addArgument(name, dscp);
                semanticStack.push(function);
                break;
            }
            case "completeFunctionDCL": {
                Block block = (Block) semanticStack.pop();
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.setBlock(block);
                SymbolTable.getInstance().getFunction(function.getName(), function.getArgumentTypes()).setBlock(block);
                SymbolTable.getInstance().getFunction(function.getName(), function.getArgumentTypes()).setReturns(function.getReturns());
                semanticStack.push(function);
                SymbolTable.getInstance().getFunction(function.getName(), function.getArgumentTypes()).setSignatureDeclared(false);
                SymbolTable.getInstance().setLastFunction(null);
                SymbolTable.getInstance().popScope();
                break;
            }
            case "addFunctionDCL": {
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.setSignature();
                FunctionDCL dupFunc = SymbolTable.getInstance().getFunction(function.getName(), function.getArgumentTypes());
                if (dupFunc == null) {
                    function.declare();
                } else if (!dupFunc.getSignatureDeclared()) {
                    throw new RuntimeException("Duplicate Function Declaration.");
                }
                semanticStack.push(function);
                break;
            }
            case "makeSimpleVarDCL": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                addAndPush(name, type);
                break;
            }
            case "constTrue": {
                Object Popped = semanticStack.pop();
                String varName;
                if (Popped instanceof NOP) {
                    varName = ((NOP) Popped).name;
                    semanticStack.push(new NOP(varName));
                } else {
                    varName = ((VarDCL) Popped).getName();
                    if (Popped instanceof SimpleVarDCL) {
                        ((SimpleVarDCL) Popped).setConstant(true);
                    }
                    semanticStack.push(Popped);
                }
                DSCP dscp = SymbolTable.getInstance().getDescriptor(varName);
                dscp.setConstant(true);
                break;
            }
            case "addToBlock": {
                Operation operation = (Operation) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                block.addOperation(operation);
                semanticStack.push(block);
                break;
            }
            case "addToGlobalBlock": {
                Declaration declaration = (Declaration) semanticStack.pop();
                if (declaration instanceof FunctionDCL)
                    addFuncToGlobalBlock((FunctionDCL) declaration);
                else
                    GlobalBlock.getInstance().addDeclaration(declaration);
                break;
            }
            case "setSignature": {
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.setSignatureDeclared(true);
                SymbolTable.getInstance().getFunction(function.getName(), function.getArgumentTypes()).setSignatureDeclared(true);
                semanticStack.push(new NOP(function.getName()));
                SymbolTable.getInstance().popScope();
                break;
            }
            case "assignValueToVar": {
                Expression exp = (Expression) semanticStack.pop();
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTable.getInstance().getDescriptor(name);
                SimpleVarDCL varDcl = new SimpleVarDCL(name, dscp.getType(), dscp.isConstant(), dscp instanceof StaticGlobalDSCP);
                varDcl.setExpression(exp);
                semanticStack.push(varDcl);
                break;
            }
            case "addVarDCL": {
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTable.getInstance().getDescriptor(name);
                SimpleVarDCL varDcl = new SimpleVarDCL(name, dscp.getType(), dscp.isConstant(), dscp instanceof StaticGlobalDSCP);
                semanticStack.push(varDcl);
                break;
            }
            case "makeSimpleAutoVarDCL": {
                Expression exp = (Expression) semanticStack.pop();
                String Name = (String) semanticStack.pop();
                SimpleVarDCL varDcl;
                if (semanticStack.peek() instanceof GlobalBlock)
                    varDcl = new SimpleVarDCL(Name, "auto", false, true, exp);
                else
                    varDcl = new SimpleVarDCL(Name, "auto", false, false, exp);
                varDcl.declare();
                semanticStack.push(varDcl);
                break;
            }
            case "dimensionIncrement": {
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(flag);
                break;
            }
            case "arrayDCL": {
                String name = (String) lexical.currentToken().getValue();
                Byte flag = (Byte) semanticStack.pop();
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                ArrayDCL.declare(name, type, new ArrayList<>(), flag, semanticStack.peek() instanceof GlobalBlock);
                semanticStack.push(new NOP(name));
                break;
            }
            case "arrayDCLUsingLastType": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymbolTable.getTypeFromStr(lastSeenType);
                ArrayDCL.declare(name, type, new ArrayList<>(), lastSeenFlag, semanticStack.peek() instanceof GlobalBlock);
                semanticStack.push(new NOP(name));
                break;
            }
            case "makeArrayVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                ArrayList<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                String name = "";
                if (semanticStack.peek() instanceof Array)
                    name = ((Array) semanticStack.pop()).getName();
                else if (semanticStack.peek() instanceof NOP)
                    name = ((NOP) semanticStack.pop()).name;

                DSCP dscp = SymbolTable.getInstance().getDescriptor(name);
                if (!dscp.getType().equals(type))
                    throw new RuntimeException("Mismatching Types.");
                ArrayDCL arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    if (((StaticGlobalArrayDSCP) dscp).getDimension() != flag)
                        throw new RuntimeException("Number Of Dimensions Does Not Match.");
                    arrDcl = new ArrayDCL(name, type, true, flag);
                    ((StaticGlobalArrayDSCP) dscp).setDimensionList(expressionList);
                } else {
                    if (((DynamicLocalArrayDSCP) dscp).getDimension() != flag)
                        throw new RuntimeException("Number Of Dimensions Does Not Match.");
                    arrDcl = new ArrayDCL(name, type, false, flag);
                    ((DynamicLocalArrayDSCP) dscp).setDimensionList(expressionList);
                }
                Collections.reverse(expressionList);
                arrDcl.setDimensionsExpression(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            case "makeAutoArrVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                ArrayList<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                String name = (String) semanticStack.pop();
                ArrayDCL arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    arrDcl = new ArrayDCL(name, type, true, expressionList.size());
                    ArrayDCL.declare(name, type, expressionList, expressionList.size(), true);
                } else {
                    arrDcl = new ArrayDCL(name, type, false, expressionList.size());
                    ArrayDCL.declare(name, type, expressionList, expressionList.size(), false);
                }
                arrDcl.setDimensionsExpression(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            case "divide": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Divide(first, second));
                break;
            }
            case "minus": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Minus(first, second));
                break;
            }
            case "multiply": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Mult(first, second));
                break;
            }
            case "mod": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Mod(first, second));
                break;
            }
            case "plus": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Plus(first, second));
                break;
            }
            case "and": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new And(first, second));
                break;
            }
            case "bitwiseAnd": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseAnd(first, second));
                break;
            }
            case "greaterThanOrEqualTo": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new GreaterThanOrEqualTo(first, second));
                break;
            }
            case "greaterThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new GreaterThan(first, second));
                break;
            }
            case "equal": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Equal(first, second));
                break;
            }
            case "lessThanOrEqualTo": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessThanOrEqualTo(first, second));
                break;
            }
            case "lessThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessThan(first, second));
                break;
            }
            case "notEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new NotEqual(first, second));
                break;
            }
            case "or": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Or(first, second));
                break;
            }
            case "bitwiseOr": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseOr(first, second));
                break;
            }
            case "bitwiseXor": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseXor(first, second));
                break;
            }
            case "bitwiseNot": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseNot(exp));
                break;
            }
            case "cast": {
                Expression exp = (Expression) semanticStack.pop();
                Type newType = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                semanticStack.push(new Cast(exp, newType));
                break;
            }
            case "negative": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Negative(exp));
                break;
            }
            case "not": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Not(exp));
                break;
            }
            case "postMinusMinus": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof Record)
                    throw new RuntimeException("Undefined Operand For Record Type.");
                checkAssign(var);
                semanticStack.push(new PostMinusMinus(var));
                break;
            }
            case "postPlusPlus": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof Record)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PostPlusPlus(var));
                break;
            }
            case "preMinusMinus": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof Record)
                    throw new RuntimeException("Undefined operand for record type" +
                            "");
                checkAssign(var);
                semanticStack.push(new PreMinusMinus(var));
                break;
            }
            case "prePlusPlus": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof Record)
                    throw new RuntimeException("Undefined Operand For Record Type.");
                checkAssign(var);
                semanticStack.push(new PrePlusPlus(var));
                break;
            }
            case "pushReal": {
                Object realNum = lexical.currentToken().getValue();
                if (realNum instanceof Float)
                    semanticStack.push(new FloatConstExp((Float) realNum));
                else
                    semanticStack.push(new DoubleConstExp((Double) realNum));
                break;
            }
            case "pushDec": {
                Object integerNum = lexical.currentToken().getValue();
                if (integerNum instanceof Integer)
                    semanticStack.push(new IntegerConstExp((Integer) integerNum));
                else
                    semanticStack.push(new LongConstExp((Long) integerNum));
                break;
            }
            case "pushBool": {
                Object value = lexical.currentToken().getValue();
                semanticStack.push(new BooleanConstExp((Boolean) value));
                break;
            }
            case "pushChar": {
                semanticStack.push(new CharConstExp((Character) lexical.currentToken().getValue()));
                break;
            }
            case "pushString": {
                semanticStack.push(new StringConstExp((String) lexical.currentToken().getValue()));
                break;
            }
            case "pushVariable": {
                String name = (String) lexical.currentToken().getValue();
                if (SymbolTable.getInstance().getFuncNames().contains(name)) {
                    semanticStack.push(name);
                    break;
                }
                DSCP dscp = SymbolTable.getInstance().getDescriptor(name);
                if (dscp instanceof StaticGlobalVariableDSCP || dscp instanceof DynamicLocalVariableDSCP)
                    semanticStack.push(new SimpleVariable(name, dscp.getType()));
                else if (dscp instanceof StaticGlobalArrayDSCP || dscp instanceof DynamicLocalArrayDSCP)
                    semanticStack.push(new Array(name, new ArrayList<>(), dscp.getType()));

                break;
            }
            case "flagIncrement": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "pushArrayVar": {
                Byte flag = (Byte) semanticStack.pop();
                ArrayList<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }

                Array var = (Array) semanticStack.pop();
                var.setIndexesExpression(expressionList);
                semanticStack.push(var);
                break;
            }
            case "assign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new Assign(var, exp));
                break;
            }
            case "plusAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new PlusAssign(var, exp));
                break;
            }
            case "minusAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MinusAssign(var, exp));
                break;
            }
            case "divideAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new DivideAssign(var, exp));
                break;
            }
            case "multAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MultAssign(var, exp));
                break;
            }
            case "modAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new ModAssign(var, exp));
                break;
            }
            case "check2types": {
                Type type = SymbolTable.getTypeFromStr((String) semanticStack.pop());
                Variable variable = (Variable) semanticStack.pop();
                if (!(variable instanceof Array))
                    throw new RuntimeException("New Is Used For Non Array Variable");
                if (variable.getType() != null && !type.equals(variable.getType()))
                    throw new RuntimeException("Mismatching Types.");
                semanticStack.push(variable);
                break;
            }
            case "voidReturn": {
                Block block = (Block) semanticStack.pop();
                FunctionDCL functionDcl = SymbolTable.getInstance().getLastFunction();
                FunctionReturn funcReturn = new FunctionReturn(null, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "return": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                FunctionDCL functionDcl = SymbolTable.getInstance().getLastFunction();
                FunctionReturn funcReturn = new FunctionReturn(exp, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "break": {
                semanticStack.push(new Break());
                break;
            }
            case "continue": {
                semanticStack.push(new Continue());
                break;
            }
            case "functionCall": {
                String name = (String) semanticStack.pop();
                semanticStack.push(new FunctionCall(name, new ArrayList<>()));
                break;
            }
            case "addArgForFuncCall": {
                Expression exp = (Expression) semanticStack.pop();
                FunctionCall funcCall = (FunctionCall) semanticStack.pop();
                funcCall.addArgument(exp);
                semanticStack.push(funcCall);
                break;
            }
            case "addLoopScope": {
                SymbolTable.getInstance().addScope(Scope.LOOP);
                break;
            }

            case "changeTop": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "trueInitialFlag": {
                InitialExpression initExp = (InitialExpression) semanticStack.pop();
                semanticStack.pop();
                Byte flag = 1;
                semanticStack.push(initExp);
                semanticStack.push(flag);
                break;
            }
            case "trueStepFlag": {
                StepExpression stepExp = (StepExpression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                if (flag == 0)
                    flag = 2;
                else
                    flag = 3;
                semanticStack.push(stepExp);
                semanticStack.push(flag);
                break;
            }
            case "for": {
                Block block = (Block) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                InitialExpression initExp = null;
                Expression exp;
                StepExpression stepExp = null;
                if (flag == 0) {
                    exp = (Expression) semanticStack.pop();
                } else if (flag == 1) {
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitialExpression) semanticStack.pop();
                } else if (flag == 2) {
                    stepExp = (StepExpression) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                } else {
                    stepExp = (StepExpression) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitialExpression) semanticStack.pop();
                }
                semanticStack.push(new For(block, initExp, exp, stepExp));
                break;
            }
            case "repeat": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                semanticStack.push(new Repeat(block, exp));
                break;
            }
            case "foreach": {
                Block block = (Block) semanticStack.pop();
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Repeat(block, exp));
                break;
            }
            /* --------------------- conditions --------------------- */
            /* --------------------- if --------------------- */
            case "if": {
                Block block = (Block) semanticStack.pop();
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new If(exp, block, null));
                break;
            }
            case "else": {
                Block block = (Block) semanticStack.pop();
                If ifSt = (If) semanticStack.pop();
                ifSt.setElseBlock(block);
                semanticStack.push(ifSt);
                break;
            }
            case "addConditionScope": {
                SymbolTable.getInstance().addScope(Scope.CONDITION);
                break;
            }
            case "popScope": {
                SymbolTable.getInstance().popScope();
                break;
            }

            /* --------------------- switch --------------------- */
            case "switch": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Switch(exp, new ArrayList<>(), null));
                break;
            }
            case "addCase": {
                Block block = (Block) semanticStack.pop();
                IntegerConstExp intConst = (IntegerConstExp) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                Case caseSt = new Case(intConst, block);
                switchSt.addCase(caseSt);
                semanticStack.push(switchSt);
                break;
            }
            case "addDefault": {
                Block defaultBlock = (Block) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                switchSt.setDefaultBlock(defaultBlock);
                semanticStack.push(switchSt);
                break;
            }
            case "addSwitchScope": {
                SymbolTable.getInstance().addScope(Scope.SWITCH);
                break;
            }

            /* --------------------- special method calls --------------------- */
            case "printExpression": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Println(expression));
                break;
            }
            case "print": {
                semanticStack.push(new Println(null));
                break;
            }
            case "inputAndCast": {
                String type = (String) lexical.currentToken().getValue();
                semanticStack.push(new Input(SymbolTable.getTypeFromStr(type)));
                break;
            }
            case "input": {
                semanticStack.push(new Input(null));
                break;
            }
            case "len": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Len(expression));
                break;
            }
            case "sizeOf": {
                String baseType = (String) semanticStack.pop();
                semanticStack.push(new SizeOf(baseType));
                break;
            }
            case "setLastSeenType": {
                lastSeenType = (String) semanticStack.pop();
                break;
            }
            case "setLastSeenFlag": {
                lastSeenFlag = (Byte) semanticStack.pop();
                break;
            }
            case "makeSimpleVarUsingLastType": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymbolTable.getTypeFromStr(lastSeenType);
                addAndPush(name, type);
                break;
            }

            default:
                throw new RuntimeException("Illegal Semantic : " + sem);
        }
    }

    private void addAndPush(String name, Type type) {
        if (semanticStack.peek() instanceof GlobalBlock)
            SymbolTable.getInstance().addVariable(name, new StaticGlobalVariableDSCP(type, false, false));
        else {

            SymbolTable.getInstance().addVariable(name, new DynamicLocalVariableDSCP(type, false,
                    SymbolTable.getInstance().getIndex(), false));

        }
        semanticStack.push(new NOP(name));
        return;
    }

    private void addFuncToGlobalBlock(FunctionDCL function) {
        if (GlobalBlock.getInstance().getDeclarations().contains(function)) {
            int index = GlobalBlock.getInstance().getDeclarations().indexOf(function);
            FunctionDCL lastFunc = (FunctionDCL) GlobalBlock.getInstance().getDeclarations().get(index);
            if (lastFunc.getBlock() == null && function.getBlock() != null && lastFunc.getSignatureDeclared()) {
                GlobalBlock.getInstance().getDeclarations().remove(lastFunc);
                GlobalBlock.getInstance().addDeclaration(function);
            } else if (lastFunc.getBlock() != null && lastFunc.getBlock() == null) {
            } else
                throw new RuntimeException("Can not have two function definitions");
        } else {

            GlobalBlock.getInstance().addDeclaration(function);
        }

    }

    private void checkAssign(Variable variable) {
        if (variable instanceof Array) {
            Array var = (Array) variable;
            int numberOfExp = var.getIndexesExpression().size();
            DSCP dscp = SymbolTable.getInstance().getDescriptor(var.getName());
            if (dscp instanceof StaticGlobalArrayDSCP) {
                if (((StaticGlobalArrayDSCP) dscp).getDimension() != numberOfExp)
                    throw new RuntimeException("Can not assign this expression");
            }
            if (dscp instanceof DynamicLocalArrayDSCP) {
                if (((DynamicLocalArrayDSCP) dscp).getDimension() != numberOfExp)
                    throw new RuntimeException("Can not assign this expression");
            }
        }
    }
}


class NOP implements Operation, Declaration {

    String name;

    public NOP(String name) {
        this.name = name;
    }

    public NOP() {
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {

    }
}

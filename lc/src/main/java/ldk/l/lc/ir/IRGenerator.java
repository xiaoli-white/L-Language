package ldk.l.lc.ir;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.expression.literal.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.file.LCSourceFileProxy;
import ldk.l.lc.ast.statement.LCAssert;
import ldk.l.lc.ast.statement.LCInit;
import ldk.l.lc.ast.statement.LCReturn;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.semantic.types.*;
import ldk.l.lc.token.Token;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.MethodKind;
import ldk.l.lc.util.symbol.MethodSymbol;
import ldk.l.lc.util.symbol.Symbol;
import ldk.l.lc.util.symbol.VariableSymbol;
import ldk.l.lc.util.symbol.object.*;
import ldk.l.lg.ir.IRBuilder;
import ldk.l.lg.ir.IRDumper;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.*;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.operand.*;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.*;
import ldk.l.lg.ir.value.IRLocalVariableReference;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.*;
import ldk.l.lg.ir.value.constant.IRConstant;
import ldk.l.util.option.Options;

import java.util.*;

import static ldk.l.lc.ir.IRUtils.parseType;

public final class IRGenerator extends LCAstVisitor {
    private IRModule module;
    private final Options options;
    private final ErrorStream errorStream;
    private IRBuilder builder;
    private IRFunction currentFunction;
    private LCAst ast = null;
    @Deprecated
    private IRControlFlowGraph initCFG = null;
    @Deprecated
    private List<IRField> initFields = null;
    @Deprecated
    private Map<String, Stack<String>> initVariableName2FieldName = null;
    @Deprecated
    private Map<String, Long> initCountOfSameNameVariables = null;
    @Deprecated
    private boolean inInit = false;
    @Deprecated
    private IRControlFlowGraph staticInitCFG = null;
    @Deprecated
    private List<IRField> staticInitFields = null;
    @Deprecated
    private Map<String, Stack<String>> staticInitVariableName2FieldName = null;
    @Deprecated
    private Map<String, Long> staticInitCountOfSameNameVariables = null;
    @Deprecated
    private boolean inStaticInit = false;
    @Deprecated
    private final Stack<IROperand> operandStack = new Stack<>();
    @Deprecated
    private IRControlFlowGraph currentCFG = null;
    @Deprecated
    private long registersCount = 0;
    @Deprecated
    private final List<IRInvoke> stringConstantInitInvocations = new ArrayList<>();
    @Deprecated
    private final List<IRInvoke> objectStaticInitInvocations = new ArrayList<>();
    @Deprecated
    private final Map<String, String> stringConstant2GlobalDataName = new HashMap<>();
    @Deprecated
    public final Map<IRControlFlowGraph, Map<String, String>> label2BasicBlock = new HashMap<>();
    @Deprecated
    private final Map<IRControlFlowGraph, Map<String, String>> label2LoopBegin = new HashMap<>();
    @Deprecated
    private final Map<IRControlFlowGraph, Map<String, String>> label2LoopEnd = new HashMap<>();
    @Deprecated
    private final Map<IRControlFlowGraph, Map<LCAbstractLoop, String>> abstractLoop2VLabel = new HashMap<>();
    private Map<String, Stack<String>> variableName2LocalName = new HashMap<>();
    private long basicBlockCount;
    private long registerCount;
    private Stack<Object> stack = new Stack<>();

    public IRGenerator(IRModule module, Options options, ErrorStream errorStream) {
        this.module = module;
        this.options = options;
        this.errorStream = errorStream;
    }

    private String generateBasicBlockName() {
        return "basicBlock_" + (basicBlockCount++);
    }

    private String generateRegisterName() {
        return String.valueOf(currentFunction.registerCount++);
    }

    private IRBasicBlock createBasicBlock() {
        IRBasicBlock basicBlock = new IRBasicBlock(generateBasicBlockName());
        currentFunction.addBasicBlock(basicBlock);
        builder.setInsertPoint(basicBlock);
        return basicBlock;
    }

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        this.ast = ast;
        return super.visitAst(ast, additional);
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        module = new IRModule();
        StructureDefinitionCreator structureDefinitionCreator = new StructureDefinitionCreator(module);
        structureDefinitionCreator.visitSourceCodeFile(lcSourceCodeFile, additional);
        for (LCSourceFileProxy proxy : lcSourceCodeFile.proxies.values())
            structureDefinitionCreator.visitSourceFileProxy(proxy, additional);

        DefinitionCreator definitionCreator = new DefinitionCreator(module);
        definitionCreator.visitSourceCodeFile(lcSourceCodeFile, additional);
        for (LCSourceFileProxy proxy : lcSourceCodeFile.proxies.values())
            definitionCreator.visitSourceFileProxy(proxy, additional);

        System.out.println("module(before):");
        IRDumper irDumper = new IRDumper();
        irDumper.visitModule(module, "");
        basicBlockCount = 0;
        registerCount = 0;
        super.visitSourceCodeFile(lcSourceCodeFile, additional);
        System.out.println("module(after):");
        irDumper.visitModule(module, "");
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        Map<String, String> virtualMethods = lcClassDeclaration.symbol.getVirtualMethods();
        IRStructure clazzStructure = module.structures.get("l.lang.Class");
        List<IRConstant> vtableElements = new ArrayList<>();
        for (String name : virtualMethods.values()) {
            IRFunction f = module.functions.get(name);
            if (f != null)
                vtableElements.add(new IRFunctionReference(f));
            else
                vtableElements.add(new IRNullptrConstant(new IRPointerType(IRType.getVoidType())));
        }
        IRGlobalVariable vtable = new IRGlobalVariable(List.of(), false, "<vtable " + lcClassDeclaration.getFullName() + ">", new IRArrayConstant(new IRArrayType(new IRPointerType(IRType.getVoidType()), virtualMethods.size()), vtableElements));
        module.putGlobalVariable(vtable);
        return super.visitClassDeclaration(lcClassDeclaration, additional);
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (!LCFlags.hasAbstract(lcMethodDeclaration.modifier.flags)) {
            IRFunction function = module.functions.get(lcMethodDeclaration.symbol.getFullName());
            currentFunction = function;
            function.isExtern = false;
            function.locals = new ArrayList<>();
            function.controlFlowGraph = new IRControlFlowGraph();
            function.controlFlowGraph.function = function;

            for (var arg : lcMethodDeclaration.parameterList.parameters) {
                Stack<String> stack = new Stack<>();
                stack.push(arg.name + "_0");
                variableName2LocalName.put(arg.name, stack);
            }
            builder = new IRBuilder();
            IRBasicBlock entryBasicBlock = createBasicBlock();
            visit(lcMethodDeclaration.body, additional);
            if (!lcMethodDeclaration.returnType.equals(SystemTypes.VOID) && !lcMethodDeclaration.body.statements.isEmpty() && lcMethodDeclaration.body.statements.getLast() instanceof LCExpressionStatement) {
                if (!stack.isEmpty()) builder.createReturn((IRValue) stack.pop());
            }
            variableName2LocalName.clear();
        }
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        for (LCStatement x : lcBlock.statements) {
            this.visit(x, additional);
        }
        if (this.getEnclosingMethodDeclaration(lcBlock) != null || this.getEnclosingInit(lcBlock) != null) {
            releaseScope(lcBlock.scope);
            List<Symbol> symbols = lcBlock.scope.name2symbol.values().stream().toList();
            for (int i = symbols.size() - 1; i >= 0; i--) {
                if (symbols.get(i) instanceof VariableSymbol variableSymbol)
                    variableName2LocalName.get(variableSymbol.name).pop();
            }
        }
        return null;
    }

    @Override
    public Object visitReturn(LCReturn lcReturn, Object additional) {
        if (lcReturn.returnedValue != null) {
            this.visit(lcReturn.returnedValue, additional);
            builder.createReturn((IRValue) stack.pop());
        } else {
            builder.createReturn();
        }
        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (getEnclosingMethodDeclaration(lcVariableDeclaration) != null) {
            String localName = lcVariableDeclaration.name + "_";
            if (variableName2LocalName.containsKey(lcVariableDeclaration.name)) {
                localName += variableName2LocalName.get(lcVariableDeclaration.name).size();
            } else {
                localName += "0";
                Stack<String> stack = new Stack<>();
                stack.push(localName);
                variableName2LocalName.put(lcVariableDeclaration.name, stack);
            }
            IRLocalVariable localVariable = new IRLocalVariable(parseType(module, lcVariableDeclaration.theType), localName);
            currentFunction.addLocal(localVariable);
            variableName2LocalName.get(lcVariableDeclaration.name).push(localName);
            if (lcVariableDeclaration.init != null) {
                visit(lcVariableDeclaration.init, additional);
                builder.createStore(new IRLocalVariableReference(localVariable), (IRValue) stack.pop());
            }
        }
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object additional) {
        IRBasicBlock loopBeginBasicBlock = createBasicBlock();
        visit(lcLoop.body, additional);
        builder.createGoto(loopBeginBasicBlock);
        IRBasicBlock loopEndBasicBlock = createBasicBlock();
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object additional) {
        if (lcFor.init != null) {
            this.visit(lcFor.init, additional);
        }
        IRBasicBlock condition = createBasicBlock();
        IRBasicBlock conditionEndBasicBlock;
        IRValue result;
        if (lcFor.condition != null) {
            this.visit(lcFor.condition, additional);
            result = (IRValue) stack.pop();
            conditionEndBasicBlock = builder.getInsertPoint();
            IRBasicBlock bb = createBasicBlock();
        } else {
            conditionEndBasicBlock = null;
            result = null;
        }

        this.visit(lcFor.body, additional);

        if (lcFor.increment != null) {
            this.visit(lcFor.increment, additional);
        }
        builder.createGoto(condition);

        IRBasicBlock end = createBasicBlock();

        if (conditionEndBasicBlock != null) {
            builder.setInsertPoint(conditionEndBasicBlock);
            builder.createJumpIfFalse(result, end);
        }

        builder.setInsertPoint(end);
        List<Symbol> symbols = lcFor.scope.name2symbol.values().stream().toList();
        for (int i = symbols.size() - 1; i >= 0; i--) {
            if (symbols.get(i) instanceof VariableSymbol variableSymbol)
                variableName2LocalName.get(variableSymbol.name).pop();
        }

//        if (irConditionalJump != null) {
//            irConditionalJump.target = end.name;
//        }
//        for (var label : lcFor.labels) {
//            this.label2LoopBegin.get(currentCFG).put(label, condition.name);
//            this.label2LoopEnd.get(currentCFG).put(label, end.name);
//        }
//        this.label2LoopBegin.get(currentCFG).put(vLabel, condition.name);
//        this.label2LoopEnd.get(currentCFG).put(vLabel, end.name);
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object additional) {
        IRBasicBlock condition = createBasicBlock();
        visit(lcWhile.condition, additional);
        IRValue result = (IRValue) stack.pop();
        IRBasicBlock conditionEndBasicBlock = builder.getInsertPoint();

        IRBasicBlock bb = createBasicBlock();
        visit(lcWhile.body, additional);
        builder.createGoto(condition);

        IRBasicBlock end = createBasicBlock();
        builder.setInsertPoint(conditionEndBasicBlock);
        builder.createJumpIfFalse(result, end);
        builder.setInsertPoint(end);
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        IRBasicBlock bodyBegin = createBasicBlock();
        visit(lcDoWhile.body, additional);
        visit(lcDoWhile.condition, additional);
        builder.createJumpIfTrue((IRValue) stack.pop(), bodyBegin);
        IRBasicBlock end = createBasicBlock();
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        visit(lcIf.condition, additional);
        IRValue cond = (IRValue) stack.pop();
        var last = builder.getInsertPoint();
        var trueBasicBlock = new IRBasicBlock(generateBasicBlockName());
        currentFunction.addBasicBlock(trueBasicBlock);
        builder.setInsertPoint(trueBasicBlock);
        visit(lcIf.then, additional);
        var thenEnd = builder.getInsertPoint();
        var nextBasicBlock = new IRBasicBlock(generateBasicBlockName());
        builder.setInsertPoint(last);
        builder.createJumpIfFalse(cond, nextBasicBlock);
        currentFunction.addBasicBlock(nextBasicBlock);
        if (lcIf._else != null) {
            boolean hasResult = (lcIf.then instanceof LCExpressionStatement) && (lcIf._else instanceof LCExpressionStatement);
            IRValue trueResult;
            if (hasResult) {
                trueResult = (IRValue) stack.pop();
            } else {
                trueResult = null;
            }
            builder.setInsertPoint(nextBasicBlock);
            visit(lcIf._else, additional);
            var elseEnd = builder.getInsertPoint();
            var endBasicBlock = new IRBasicBlock(generateBasicBlockName());
            currentFunction.addBasicBlock(endBasicBlock);
            builder.setInsertPoint(thenEnd);
            builder.createGoto(endBasicBlock);
            builder.setInsertPoint(endBasicBlock);
            if (hasResult) {
                IRValue falseResult = (IRValue) stack.pop();
                Map<IRBasicBlock, IRValue> map = new LinkedHashMap<>();
                map.put(thenEnd, trueResult);
                map.put(elseEnd, falseResult);
                stack.push(builder.createPhi(map));
            }
        } else {
            builder.setInsertPoint(nextBasicBlock);
        }
        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        if (lcBinary._operator == Tokens.Operator.Dot || lcBinary._operator == Tokens.Operator.MemberAccess) {
            this.visit(lcBinary.expression1, additional);
            Type type = lcBinary.expression1.theType;
            if (type instanceof NullableType nullableType) type = nullableType.base;
            if (lcBinary._operator == Tokens.Operator.MemberAccess) {
                type = ((PointerType) type).base;
                IRValue value = (IRValue) stack.pop();
                stack.push(builder.createLoad(value));
            }

            if (type instanceof ArrayType arrayType && lcBinary.expression2 instanceof LCVariable lcVariable && "length".equals(lcVariable.name)) {
//                IROperand array = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
//                arrayLength(array, arrayType.base);
            } else {
                if (lcBinary.expression2 instanceof LCMethodCall lcMethodCall) {
                    if (lcMethodCall.symbol != null && lcMethodCall.symbol.methodKind == MethodKind.Destructor && lcBinary.expression1 instanceof LCSuper lcSuper) {
                        IRValue operand = (IRValue) stack.pop();
                        callFunction(parseType(module, lcMethodCall.symbol.returnType), new IRFunctionReference(module.functions.get(lcMethodCall.symbol.getFullName())), List.of(operand));
                    } else {
                        callMethod(lcMethodCall, false);
                    }
                } else if (lcBinary.expression2 instanceof LCVariable lcVariable) {
                    getVariable(lcVariable.symbol, lcVariable.isLeftValue, false);
                } else {
                    this.visit(lcBinary.expression2, additional);
                }
            }
        } else if (Token.isArithmeticOperator(lcBinary._operator)) {
            visit(lcBinary.expression1, additional);
            IRValue left = (IRValue) stack.pop();
            visit(lcBinary.expression2, additional);
            IRValue right = (IRValue) stack.pop();
            if (lcBinary.methodSymbol != null) {
                callMethod(lcBinary.methodSymbol, List.of(left, right));
            } else if (lcBinary._operator == Tokens.Operator.Plus) {
                if (lcBinary.expression1.theType instanceof PointerType pointerType) {
                    IRRegister tmp1 = builder.createBitCast(left, IRType.getUnsignedLongType());
                    IRRegister tmp2 = builder.createMul(right, new IRIntegerConstant((IRIntegerType) right.getType(), parseType(module, pointerType.base).getLength()));
                    IRRegister result = builder.createAdd(tmp1, tmp2);
                    stack.push(builder.createBitCast(result, left.getType()));
                } else {
                    stack.push(builder.createAdd(left, right));
                }
            } else if (lcBinary._operator == Tokens.Operator.Minus) {
                if (lcBinary.expression1.theType instanceof PointerType pointerType) {
                    IRRegister tmp1 = builder.createBitCast(left, IRType.getUnsignedLongType());
                    IRRegister tmp2 = builder.createMul(right, new IRIntegerConstant((IRIntegerType) right.getType(), parseType(module, pointerType.base).getLength()));
                    IRRegister result = builder.createSub(tmp1, tmp2);
                    stack.push(builder.createBitCast(result, left.getType()));
                } else {
                    stack.push(builder.createSub(left, right));
                }
            } else {
                IRRegister result = new IRRegister(generateRegisterName());
                IRBinaryOperates.Operator op = switch (lcBinary._operator) {
                    case Multiply -> IRBinaryOperates.Operator.MUL;
                    case Divide -> IRBinaryOperates.Operator.DIV;
                    case Modulus -> IRBinaryOperates.Operator.MOD;
                    case BitAnd -> IRBinaryOperates.Operator.AND;
                    case BitOr -> IRBinaryOperates.Operator.OR;
                    case BitXor -> IRBinaryOperates.Operator.XOR;
                    case LeftShiftArithmetic -> IRBinaryOperates.Operator.SHL;
                    case RightShiftArithmetic -> IRBinaryOperates.Operator.SHR;
                    case RightShiftLogical -> IRBinaryOperates.Operator.USHR;
                    default -> throw new IllegalArgumentException("unsupported operator: " + lcBinary._operator);
                };
                builder.getInsertPoint().instructions.add(new IRBinaryOperates(op, left, right, result));
                stack.push(result);
            }
        } else if (Token.isLogicalOperator(lcBinary._operator)) {
            if (lcBinary._operator == Tokens.Operator.And) {
                visit(lcBinary.expression1, additional);
                IRValue left = (IRValue) stack.pop();
                IRBasicBlock leftEndBlock = builder.getInsertPoint();
                IRBasicBlock rightBeginBlock = createBasicBlock();
                visit(lcBinary.expression2, additional);
                IRValue right = (IRValue) stack.pop();
                IRBasicBlock rightEndBlock = builder.getInsertPoint();
                IRBasicBlock end = createBasicBlock();
                builder.setInsertPoint(leftEndBlock);
                builder.createJumpIfFalse(left, end);
                builder.setInsertPoint(end);
                Map<IRBasicBlock, IRValue> map = new LinkedHashMap<>();
                map.put(leftEndBlock, left);
                map.put(rightEndBlock, right);
                stack.push(builder.createPhi(map));
            } else if (lcBinary._operator == Tokens.Operator.Or) {
                visit(lcBinary.expression1, additional);
                IRValue left = (IRValue) stack.pop();
                IRBasicBlock leftEndBlock = builder.getInsertPoint();
                IRBasicBlock rightBeginBlock = createBasicBlock();
                visit(lcBinary.expression2, additional);
                IRValue right = (IRValue) stack.pop();
                IRBasicBlock rightEndBlock = builder.getInsertPoint();
                IRBasicBlock end = createBasicBlock();
                builder.setInsertPoint(leftEndBlock);
                builder.createJumpIfTrue(left, end);
                builder.setInsertPoint(end);
                Map<IRBasicBlock, IRValue> map = new LinkedHashMap<>();
                map.put(leftEndBlock, left);
                map.put(rightEndBlock, right);
                stack.push(builder.createPhi(map));
            }
        } else if (Token.isRelationOperator(lcBinary._operator)) {
            visit(lcBinary.expression1, additional);
            IRValue left = (IRValue) stack.pop();
            visit(lcBinary.expression2, additional);
            IRValue right = (IRValue) stack.pop();
            IRRegister resultRegister = new IRRegister(generateRegisterName());
            builder.getInsertPoint().instructions.add(new IRCompare(parseRelationOperator(lcBinary._operator), left, right, resultRegister));
            stack.push(resultRegister);
        } else if (Token.isAssignOperator(lcBinary._operator)) {
            visit(lcBinary.expression1, additional);
            IRValue left = (IRValue) stack.pop();
            visit(lcBinary.expression2, additional);
            IRValue right = (IRValue) stack.pop();
            IRValue result;
            switch (lcBinary._operator) {
                case Assign -> {
                    result = right;
                    retain(right, lcBinary.expression2.theType);
//                    var tmp = builder.createLoad(left);
//                    release(tmp, lcBinary.expression1.theType);
                }
                case PlusAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createAdd(leftVal, right);
                }
                case MinusAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createSub(leftVal, right);
                }
                case MultiplyAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createMul(leftVal, right);
                }
                case DivideAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createDiv(leftVal, right);
                }
                case ModulusAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createMod(leftVal, right);
                }
                case LeftShiftArithmeticAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createShl(leftVal, right);
                }
                case RightShiftArithmeticAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createShr(leftVal, right);
                }
                case RightShiftLogicalAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createUShr(leftVal, right);
                }
                case BitAndAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createAnd(leftVal, right);
                }
                case BitOrAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createOr(leftVal, right);
                }
                case BitXorAssign -> {
                    IRValue leftVal = builder.createLoad(left);
                    result = builder.createXor(leftVal, right);
                }
                case null, default -> throw new RuntimeException("Unsupported operator: " + lcBinary._operator);
            }
            builder.createStore(left, result);
            stack.push(result);
        }
        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object additional) {
        this.visit(lcUnary.expression, additional);
        IRValue value = (IRValue) stack.pop();

        if (lcUnary.methodSymbol != null) {
//            IRType type = parseType(lcUnary.expression.theType);
            IRValue val;
            if (lcUnary._operator == Tokens.Operator.Inc || lcUnary._operator == Tokens.Operator.Dec) {
                val = builder.createLoad(value);
            } else {
                val = value;
            }
//            callMethod(lcUnary.methodSymbol, List.of(irOperand), List.of(lcUnary.expression.theType));
            if (SystemTypes.VOID.equals(lcUnary.methodSymbol.returnType)) {
                IRValue result = (IRValue) stack.pop();
                if (lcUnary._operator == Tokens.Operator.Inc || lcUnary._operator == Tokens.Operator.Dec) {
                    builder.createStore(value, result);
                    stack.push(lcUnary.isPrefix ? result : val);
                } else {
                    stack.push(val);
                }
            }
        } else if (lcUnary.isPrefix) {
            if (lcUnary._operator == Tokens.Operator.Plus) {
                stack.push(value);
            } else {
                if (lcUnary._operator == Tokens.Operator.Inc) {
                    stack.push(builder.createInc(value));
                } else if (lcUnary._operator == Tokens.Operator.Dec) {
                    stack.push(builder.createDec(value));
                } else if (lcUnary._operator == Tokens.Operator.Minus) {
                    stack.push(builder.createNeg(value));
                } else if (lcUnary._operator == Tokens.Operator.BitNot) {
                    stack.push(builder.createNot(value));
                }
            }
        } else {
            var tmp = builder.createLoad(value);
            if (lcUnary._operator == Tokens.Operator.Inc) {
                builder.createInc(value);
            } else if (lcUnary._operator == Tokens.Operator.Dec) {
                builder.createDec(value);
            } else {
                throw new IllegalArgumentException("Unknown unary operator: " + lcUnary._operator);
            }
            stack.push(tmp);
        }
        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        String typeName = lcNewObject.theType.toTypeString();

        IRValue place;
        if (lcNewObject.place != null) {
            this.visit(lcNewObject.place, additional);
            place = (IRValue) stack.pop();
            retain(place, lcNewObject.theType);
        } else {
            IRStructure structure = module.structures.get(typeName);
            IRValue ptr = builder.createInvoke(module.functions.get("malloc"), List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), structure.getLength())));
            place = builder.createPointerToPointer(ptr, new IRPointerType(new IRStructureType(structure)));
            var refCountPtr = builder.createGetElementPointer(place, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), structure.getFieldIndex("<ref_count>"))));
            builder.createInc(refCountPtr);
        }

        retain(place, lcNewObject.theType);
        builder.createInvoke(module.functions.get(typeName + ".<__init__>()V"), List.of(place));

        List<IRValue> args = new ArrayList<>(lcNewObject.arguments.size() + 1);
        args.add(place);
        retain(place, lcNewObject.theType);
        for (LCExpression argument : lcNewObject.arguments) {
            Type type = argument.theType;
            this.visit(argument, additional);
            IRValue arg = (IRValue) stack.pop();
            retain(arg, type);
            args.add(arg);
        }
        builder.createInvoke(module.functions.get(typeName + ".<__init__>()V"), args);

        stack.push(place);

        return null;
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object additional) {
        if (lcNewArray.place != null) {
            this.visit(lcNewArray.place, additional);
        } else {
            IRValue length;
            if (lcNewArray.elements != null) {
                length = new IRIntegerConstant(IRType.getUnsignedLongType(), lcNewArray.elements.size());
            } else {
                this.visit(lcNewArray.dimensions.getFirst(), additional);
                length = (IRValue) stack.pop();
            }
            newArray((ArrayType) lcNewArray.theType, length);
        }
        IRValue place = (IRValue) stack.pop();
        retain(place, lcNewArray.theType);


        IRStructure arrayStructure = ((IRStructureType) ((IRPointerType) place.getType()).base).structure;

        if (lcNewArray.elements != null) {
            var lengthPtr = builder.createGetElementPointer(place, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), arrayStructure.getFieldIndex("length"))));
            builder.createStore(lengthPtr, new IRIntegerConstant(IRType.getUnsignedLongType(), lcNewArray.elements.size()));
            var dataPtr = builder.createGetElementPointer(place, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), arrayStructure.getFieldIndex("data"))));
            for (int i = 0; i < lcNewArray.elements.size(); i++) {
                LCExpression element = lcNewArray.elements.get(i);
                this.visit(element, additional);
                IRValue elem = (IRValue) stack.pop();
                retain(elem, element.theType);
                builder.createStore(builder.createGetElementPointer(dataPtr, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), i))), elem);
            }
        } else {
            initArray((ArrayType) lcNewArray.theType, place, lcNewArray.dimensions, 0);
        }
        stack.push(place);
        return null;
    }

    @Override
    public Object visitDelete(LCDelete lcDelete, Object additional) {
        this.visit(lcDelete.expression, additional);
        IRValue value = (IRValue) stack.pop();
        deleteSomething(value, lcDelete.expression.theType);
        return null;
    }

    @Override
    public Object visitClassof(LCClassof lcClassof, Object additional) {
        stack.push(new IRGlobalVariableReference(module.globals.get(("<class_instance " + lcClassof.typeExpression.theType.toTypeString() + ">"))));
        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object additional) {
        this.visit(lcArrayAccess.base, additional);
        IRValue base = (IRValue) stack.pop();

        this.visit(lcArrayAccess.index, additional);
        IRValue index = (IRValue) stack.pop();

        IRType elementType = parseType(module, lcArrayAccess.theType);
        if (lcArrayAccess.base.theType instanceof ArrayType) {
            arrayLength(base);
            IRValue length = (IRValue) stack.pop();
            var prev1 = builder.getInsertPoint();
            var bb = createBasicBlock();
            var index2 = builder.createAdd(index, length);
            var next1 = createBasicBlock();
            builder.setInsertPoint(prev1);
            builder.createJumpIfGreaterEqual(index, new IRIntegerConstant(IRType.getUnsignedLongType(), 0), next1);
            builder.setInsertPoint(next1);
            var i = builder.createPhi(Map.of(prev1, index, bb, index2));
            var bb2 = createBasicBlock();
            createBasicBlock();
            IRValue dataPtr = builder.createGetElementPointer(base, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), ((IRStructureType) ((IRPointerType) base.getType()).base).structure.getFieldIndex("data"))));
            IRValue valuePtr = builder.createGetElementPointer(dataPtr, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), i));
            if (lcArrayAccess.isLeftValue) {
                stack.push(valuePtr);
            } else {
                stack.push(builder.createLoad(valuePtr));
            }
            var next2 = createBasicBlock();
            builder.setInsertPoint(next1);
            builder.createJumpIfLess(i, new IRIntegerConstant(IRType.getUnsignedLongType(), 0), next2);
            builder.setInsertPoint(bb2);
            builder.createJumpIfGreaterEqual(i, length, next2);
            builder.setInsertPoint(next2);
//             TODO throw ArrayIndexOutOfBoundsException
        } else {
            IRValue valuePtr = builder.createGetElementPointer(base, List.of(index));
            if (lcArrayAccess.isLeftValue) {
                stack.push(valuePtr);
            } else {
                stack.push(builder.createLoad(valuePtr));
            }
        }
        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object additional) {
        this.visit(lcDereference.expression, additional);
        IRValue value = (IRValue) stack.pop();

        if (lcDereference.isLeftValue) {
            stack.push(value);
        } else {
            stack.push(builder.createLoad(value));
        }
        return null;
    }

    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object additional) {
        return super.visitGetAddress(lcGetAddress, additional);
    }

    @Override
    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object additional) {
        this.visit(lcNotNullAssert.base, additional);
        IRValue base = (IRValue) stack.pop();
        IRNullptrConstant nullValue = new IRNullptrConstant((IRPointerType) parseType(module, lcNotNullAssert.base.theType));
        var prev = builder.getInsertPoint();
        createBasicBlock();
        // TODO throws NullPointerException
        var end = createBasicBlock();
        builder.setInsertPoint(prev);
        builder.createJumpIfNotEqual(base, nullValue, end);
        builder.setInsertPoint(end);

        stack.push(base);
        return null;
    }

    @Override
    public Object visitAssert(LCAssert lcAssert, Object additional) {
        this.visit(lcAssert.condition, additional);
        IRValue condition = (IRValue) stack.pop();
        var prev = builder.getInsertPoint();
        createBasicBlock();
//         TODO throws AssertionError
        var end = createBasicBlock();
        builder.setInsertPoint(prev);
        builder.createJumpIfTrue(condition, end);
        builder.setInsertPoint(end);
        return null;
    }

    @Override
    public Object visitTypeof(LCTypeof lcTypeof, Object additional) {
        this.visit(lcTypeof.expression, additional);
        IRValue value = (IRValue) stack.pop();

//        IRType stringType = parseType(SystemTypes.String_Type);

        if (lcTypeof.expression.theType instanceof PointerType || lcTypeof.expression.theType instanceof ReferenceType || lcTypeof.expression.theType instanceof ArrayType || SystemTypes.isPrimitiveType(lcTypeof.expression.theType)) {
//            int index = module.constantPool.put(new IRConstantPool.Entry(stringType, lcTypeof.expression.theType.toTypeString()));
//            operandStack.push(new IRConstant(index));
        }
        return null;
    }

    @Override
    public Object visitSizeof(LCSizeof lcSizeof, Object additional) {
        if (SystemTypes.BYTE.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_BYTE.equals(lcSizeof.expression.theType) || SystemTypes.BOOLEAN.equals(lcSizeof.expression.theType)) {
            stack.push(new IRIntegerConstant(IRType.getUnsignedLongType(), 1));
        } else if (SystemTypes.SHORT.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_SHORT.equals(lcSizeof.expression.theType)) {
            stack.push(new IRIntegerConstant(IRType.getUnsignedLongType(), 2));
        } else if (SystemTypes.INT.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_INT.equals(lcSizeof.expression.theType) || SystemTypes.FLOAT.equals(lcSizeof.expression.theType) || SystemTypes.CHAR.equals(lcSizeof.expression.theType)) {
            stack.push(new IRIntegerConstant(IRType.getUnsignedLongType(), 4));
        } else if (SystemTypes.LONG.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_LONG.equals(lcSizeof.expression.theType) || SystemTypes.DOUBLE.equals(lcSizeof.expression.theType) || lcSizeof.expression.theType instanceof PointerType) {
            stack.push(new IRIntegerConstant(IRType.getUnsignedLongType(), 8));
        } else {
        }
        return null;
    }

    @Override
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object additional) {
        stack.push(new IRIntegerConstant((IRIntegerType) parseType(module, lcIntegerLiteral.theType), lcIntegerLiteral.value));
        return null;
    }

    @Override
    public Object visitDecimalLiteral(LCDecimalLiteral lcDecimalLiteral, Object additional) {
        if (lcDecimalLiteral.theType.equals(SystemTypes.FLOAT)) {
            stack.push(new IRFloatConstant(lcDecimalLiteral.value.floatValue()));
        } else {
            stack.push(new IRDoubleConstant(lcDecimalLiteral.value));
        }
        return null;
    }

    @Override
    public Object visitBooleanLiteral(LCBooleanLiteral lcBooleanLiteral, Object additional) {
        stack.push(new IRIntegerConstant(IRType.getBooleanType(), lcBooleanLiteral.value ? 1L : 0L));
        return null;
    }

    @Override
    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object additional) {
        stack.push(new IRNullptrConstant((IRPointerType) parseType(module, lcNullLiteral.theType)));
        return null;
    }

    @Override
    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object additional) {
        stack.push(new IRNullptrConstant((IRPointerType) parseType(module, lcNullptrLiteral.theType)));
        return null;
    }

    @Override
    public Object visitCharLiteral(LCCharLiteral lcCharLiteral, Object additional) {
        stack.push(new IRIntegerConstant(IRType.getCharType(), lcCharLiteral.value));
        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object additional) {
        getVariable(lcVariable.symbol, lcVariable.isLeftValue, true);
        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object additional) {
        getThisPtr();
        return null;
    }

    @Override
    public Object visitSuper(LCSuper lcSuper, Object additional) {
        getThisPtr();
        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object additional) {
        callMethod(lcMethodCall, true);
        return null;
    }

    private void newArray(ArrayType arrayType, IRValue length) {
        IRType elementType = parseType(module, arrayType.base);
        IRIntegerConstant typeSize = new IRIntegerConstant(IRType.getUnsignedLongType(), elementType.getLength());
        var elementsSize = builder.createMul(typeSize, length);
        var size = builder.createAdd(elementsSize, new IRIntegerConstant(IRType.getUnsignedLongType(), 16));
        var place = builder.createInvoke(module.functions.get("malloc"), List.of(size));
        stack.push(builder.createPointerToPointer(place, (IRPointerType) parseType(module, arrayType)));
    }

    private void initArray(ArrayType arrayType, IRValue array, List<LCExpression> dimensions, int index) {
        if (index + 1 >= dimensions.size()) return;

        LCExpression dimension = dimensions.get(index);
        LCExpression length = dimensions.get(index + 1);
        if (dimension == null || length == null) return;

        this.visit(dimension, null);
        IRValue dim = (IRValue) stack.pop();
        this.visit(length, null);
        IRValue len = (IRValue) stack.pop();

        var tmp = builder.createStackAlloc(IRType.getUnsignedLongType());
        builder.createStore(tmp, new IRIntegerConstant(IRType.getUnsignedLongType(), 0));
        IRValue dataPtr = builder.createGetElementPointer(array, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), new IRIntegerConstant(IRType.getUnsignedLongType(), ((IRStructureType) ((IRPointerType) array.getType()).base).structure.getFieldIndex("data"))));
        var condition = createBasicBlock();
        var i = builder.createLoad(tmp);
        createBasicBlock();
        newArray((ArrayType) arrayType.base, len);
        IRValue place = (IRValue) stack.pop();
        initArrayHead(place);
        initArray((ArrayType) arrayType.base, place, dimensions, index + 1);
        IRValue valuePtr = builder.createGetElementPointer(dataPtr, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0), i));
        builder.createStore(valuePtr, place);
        builder.createInc(tmp);
        builder.createGoto(condition);
        var end = createBasicBlock();
        builder.setInsertPoint(condition);
        builder.createJumpIfGreaterEqual(i, dim, end);
        builder.setInsertPoint(end);
    }

    private void initArrayHead(IRValue array) {

    }

    private void arrayLength(IRValue array) {
        var lengthPtr = builder.createGetElementPointer(array, List.of(new IRIntegerConstant(IRType.getUnsignedIntType(), 0), new IRIntegerConstant(IRType.getUnsignedIntType(), ((IRStructureType) ((IRPointerType) array.getType()).base).structure.getFieldIndex("length"))));
        stack.push(builder.createLoad(lengthPtr));
    }

    private void callMethod(LCMethodCall lcMethodCall, boolean needThisPtr) {
        List<IRValue> arguments = new ArrayList<>();
        if (lcMethodCall.symbol != null) {
            if (!LCFlags.hasStatic(lcMethodCall.symbol.flags)) {
                if (needThisPtr) getThisPtr();
                IRValue thisPtr = (IRValue) stack.pop();
                arguments.add(thisPtr);
            }
            for (int i = 0; i < lcMethodCall.arguments.size(); ++i) {
                visit(lcMethodCall.arguments.get(i), null);
                IRValue argument = (IRValue) stack.pop();
                arguments.add(argument);
            }
            callMethod(lcMethodCall.symbol, arguments);
        } else {
            visit(lcMethodCall.expression, null);
            IRValue func = (IRValue) stack.pop();
            for (int i = 0; i < lcMethodCall.arguments.size(); ++i) {
                visit(lcMethodCall.arguments.get(i), null);
                IRValue argument = (IRValue) stack.pop();
                arguments.add(argument);
            }
            IRRegister result = callFunction(parseType(module, lcMethodCall.theType), func, arguments);
            if (result != null) stack.push(result);
        }

    }

    private void callMethod(MethodSymbol symbol, List<IRValue> arguments) {
        IRValue func;
        if (LCFlags.hasStatic(symbol.flags) || LCFlags.hasFinal(symbol.flags) || symbol.methodKind == MethodKind.Constructor) {
            IRFunction function = module.functions.get(symbol.getFullName());
            func = new IRFunctionReference(function);
        } else {
            IRValue thisPtr = arguments.getFirst();
            IRValue voidPtr;
            switch (symbol.objectSymbol) {
                case ClassSymbol classSymbol -> {
                    var classInstancePtr = builder.createGetElementPointer(thisPtr, List.of(new IRIntegerConstant(IRType.getIntType(), 0), new IRIntegerConstant(IRType.getIntType(), ((IRStructureType) ((IRPointerType) thisPtr.getType()).base).structure.getFieldIndex("<class_ptr>"))));
                    var classInstance = builder.createLoad(classInstancePtr);
                    var vtablePtr = builder.createGetElementPointer(classInstance, List.of(new IRIntegerConstant(IRType.getIntType(), 0), new IRIntegerConstant(IRType.getIntType(), ((IRStructureType) ((IRPointerType) classInstance.getType()).base).structure.getFieldIndex("vtable"))));
                    var vtable = builder.createLoad(vtablePtr);
                    var funcPtr = builder.createGetElementPointer(vtable, List.of(new IRIntegerConstant(IRType.getIntType(), classSymbol.getVirtualMethods().keySet().stream().toList().indexOf(symbol.getSimpleName()))));
                    voidPtr = builder.createLoad(funcPtr);
                }
                case InterfaceSymbol interfaceSymbol -> {
                    voidPtr = null;
//                    String classInstanceAddressRegister = allocateVirtualRegister();
//                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), arguments.getFirst(), new IRVirtualRegister(classInstanceAddressRegister)));
//                    ClassSymbol symbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
//                    MethodSymbol methodSymbol2 = null;
//                    for (MethodSymbol method : symbol.methods) {
//                        if (method.name.equals("getITableEntry")) {
//                            methodSymbol2 = method;
//                            break;
//                        }
//                    }
//                    IRMacro interfaceClassInstance = new IRMacro("global_data_address", new String[]{"<class_instance " + interfaceSymbol.getFullName() + ">"});
//                    IRVirtualRegister classInstance = new IRVirtualRegister(classInstanceAddressRegister);
//                    retain(classInstance, SystemTypes.Class_Type);
//                    retain(interfaceClassInstance, SystemTypes.Class_Type);
//                    String itableAddressRegister = allocateVirtualRegister();
//                    addInstruction(new IRInvoke(new IRPointerType(IRType.getVoidType()), new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol2).getFullName()}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstance, interfaceClassInstance}, new IRVirtualRegister(itableAddressRegister)));
//                    String temp = allocateVirtualRegister();
//                    addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(itableAddressRegister), new IRMacro("itable_entry_offset", new String[]{interfaceSymbol.getFullName(), methodSymbol.getSimpleName()}), new IRVirtualRegister(temp)));
//                    String addressRegister = allocateVirtualRegister();
//                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp), new IRVirtualRegister(addressRegister)));
//                    address = new IRVirtualRegister(addressRegister);
                }
                case EnumSymbol enumSymbol -> {
                    voidPtr = null;
//                    address = null;
                }
                case RecordSymbol recordSymbol -> {
                    voidPtr = null;
//                    address = null;
                }
                case AnnotationSymbol annotationSymbol -> {
                    voidPtr = null;
//                    address = null;
                }
            }
            if (voidPtr == null) return;
            List<IRType> types = new ArrayList<>(arguments.size());
            for (IRValue argument : arguments) types.add(argument.getType());
            func = builder.createBitCast(voidPtr, new IRFunctionReferenceType(parseType(module, symbol.returnType), types, false));
        }
        IRRegister result = callFunction(parseType(module, symbol.theType), func, arguments);
        if (result != null) stack.push(result);
    }

    private IRRegister callFunction(IRType returnType, IRValue func, List<IRValue> arguments) {
        return builder.createInvoke(returnType, func, arguments, IRType.getVoidType().equals(returnType) ? null : generateRegisterName());
    }

    public void getVariable(VariableSymbol symbol, boolean isLeftValue, boolean needThisPtr) {
        LCMethodDeclaration methodDeclaration = this.getEnclosingMethodDeclaration(symbol.declaration);
        LCInit init = this.getEnclosingInit(symbol.declaration);
        if (methodDeclaration != null || init != null) {
            Stack<String> stack = this.variableName2LocalName.get(symbol.name);
            if (stack.isEmpty()) return;
            IRValue val = new IRLocalVariableReference(currentFunction.getLocalVariable(stack.peek()));
            if (isLeftValue) {
                this.stack.push(val);
            } else {
                this.stack.push(builder.createLoad(val));
            }
        } else {
            if (LCFlags.hasStatic(symbol.flags)) {
                IRGlobalVariableReference ref = new IRGlobalVariableReference(module.globals.get(symbol.objectSymbol.getFullName() + "." + symbol.name));
                if (isLeftValue) {
                    stack.push(ref);
                } else {
                    stack.push(builder.createLoad(ref));
                }
            } else {
                if (needThisPtr) this.getThisPtr();
                IRValue op = (IRValue) stack.pop();
                IRStructure structure = module.structures.get(symbol.objectSymbol.getFullName());
                IRValue addr = builder.createGetElementPointer(op, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0L), new IRIntegerConstant(IRType.getUnsignedLongType(), structure.getFieldIndex(symbol.name))));
                if (isLeftValue) {
                    stack.push(addr);
                } else {
                    stack.push(builder.createLoad(addr));
                }
            }
        }
    }

    private void getThisPtr() {
        stack.push(builder.createLoad(new IRLocalVariableReference(currentFunction.getLocalVariable("<this_ptr>"))));
    }

    private void releaseScope(Scope scope) {
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                if (!SystemTypes.isReference(variableSymbol.theType)) continue;
                getVariable(variableSymbol, false, true);
                IRValue operand = (IRValue) stack.pop();
                release(operand, variableSymbol.theType);
            }
        }
    }

    private void retain(IRValue operand, Type type) {
        if (!SystemTypes.isReference(type)) return;
        if (type instanceof ArrayType) return;

        IRPointerType ty;
        IRBasicBlock prevBlock;
        if (type instanceof NullableType nullableType) {
            ty = (IRPointerType) parseType(module, nullableType);
            prevBlock = builder.getInsertPoint();
            createBasicBlock();
        } else {
            ty = (IRPointerType) parseType(module, type);
            prevBlock = null;
        }
        if (ty == null) throw new RuntimeException("Invalid type");

        var refCountPtr = builder.createGetElementPointer(operand, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0L), new IRIntegerConstant(IRType.getUnsignedLongType(), ((IRStructureType) ty.base).structure.getFieldIndex("<ref_count>"))));
        builder.createInc(refCountPtr);

        var next = createBasicBlock();
        if (prevBlock != null) {
            builder.setInsertPoint(prevBlock);
            IRNullptrConstant nullValue = new IRNullptrConstant(ty);
            builder.createJumpIfEqual(operand, nullValue, next);
            builder.setInsertPoint(next);
        }
    }

    private void release(IRValue operand, Type type) {
        if (!SystemTypes.isReference(type)) return;
        if (type instanceof ArrayType) return;

        IRPointerType ty;
        IRBasicBlock prevBlock;
        if (type instanceof NullableType nullableType) {
            ty = (IRPointerType) parseType(module, nullableType);
            prevBlock = builder.getInsertPoint();
            createBasicBlock();
        } else {
            ty = (IRPointerType) parseType(module, type);
            prevBlock = null;
        }
        if (ty == null) throw new RuntimeException("Invalid type");

        var refCountPtr = builder.createGetElementPointer(operand, List.of(new IRIntegerConstant(IRType.getUnsignedLongType(), 0L), new IRIntegerConstant(IRType.getUnsignedLongType(), ((IRStructureType) ty.base).structure.getFieldIndex("<ref_count>"))));
        var refCount = builder.createDec(refCountPtr);
        var prev = builder.getInsertPoint();
        createBasicBlock();
        deleteSomething(operand, type);
        var next = createBasicBlock();
        builder.setInsertPoint(prev);
        builder.createJumpIfNotEqual(refCount, new IRIntegerConstant(IRType.getUnsignedLongType(), 0L), next);

        if (prevBlock != null) {
            builder.setInsertPoint(prevBlock);
            IRValue nullValue = new IRNullptrConstant(ty);
            builder.createJumpIfEqual(operand, nullValue, next);
            builder.setInsertPoint(next);
        }
    }

    private void deleteSomething(IRValue operand, Type type) {
        if (type instanceof ArrayType arrayType) deleteArray(operand, arrayType);
        else if (type instanceof NamedType namedType && !SystemTypes.isPrimitiveType(namedType))
            deleteObject(operand, namedType);
    }

    private void deleteObject(IRValue object, NamedType objectType) {
        var classInstancePtr = builder.createGetElementPointer(object, List.of(new IRIntegerConstant(IRType.getIntType(), 0), new IRIntegerConstant(IRType.getIntType(), ((IRStructureType) ((IRPointerType) object.getType()).base).structure.getFieldIndex("<class_ptr>"))));
        var classInstance = builder.createLoad(classInstancePtr);
        ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(ast.getObjectDeclaration(objectType.name)));
        IRFunctionReferenceType destructorType = new IRFunctionReferenceType(IRType.getVoidType(), List.of(parseType(module, objectType)), false);
        IRValue destructor;
        if (objectSymbol instanceof InterfaceSymbol interfaceSymbol) {
//            ClassSymbol classSymbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
//            MethodSymbol methodSymbol = null;
//            for (MethodSymbol method : classSymbol.methods) {
//                if (method.name.equals("getITableEntry")) {
//                    methodSymbol = method;
//                    break;
//                }
//            }
//            IRMacro interfaceClassInstance = new IRMacro("global_data_address", new String[]{"<class_instance " + interfaceSymbol.getFullName() + ">"});
//            IRVirtualRegister classInstance = new IRVirtualRegister(classInstanceAddressRegister);
//            retain(classInstance, SystemTypes.Class_Type);
//            retain(interfaceClassInstance, SystemTypes.Class_Type);
//            String itableAddressRegister = allocateVirtualRegister();
//            addInstruction(new IRInvoke(new IRPointerType(IRType.getVoidType()), new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol).getFullName()}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstance, interfaceClassInstance}, new IRVirtualRegister(itableAddressRegister)));
//            String temp = allocateVirtualRegister();
//            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(itableAddressRegister), new IRMacro("itable_entry_offset", new String[]{interfaceSymbol.getFullName(), "<deinit>()V"}), new IRVirtualRegister(temp)));
//            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp), new IRVirtualRegister(destructor)));
            destructor = null;
        } else {
            var vtablePtr = builder.createGetElementPointer(classInstance, List.of(new IRIntegerConstant(IRType.getIntType(), 0), new IRIntegerConstant(IRType.getIntType(), ((IRStructureType) ((IRPointerType) classInstance.getType()).base).structure.getFieldIndex("vtable"))));
            var vtable = builder.createLoad(vtablePtr);
            var funcPtr = builder.createGetElementPointer(vtable, List.of(new IRIntegerConstant(IRType.getIntType(), ((ClassSymbol) objectSymbol).getVirtualMethods().keySet().stream().toList().indexOf("<deinit>"))));
            var voidPtr = builder.createLoad(funcPtr);
            destructor = builder.createBitCast(voidPtr, destructorType);
        }
        builder.createInvoke(destructorType, destructor, List.of(object));
        builder.createInvoke(module.functions.get("free"), List.of(object));
    }

    private void deleteArray(IRValue array, ArrayType arrayType) {
        if (!SystemTypes.isPrimitiveType(arrayType.base)) {
//            int constantTypeSizeIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(parseType(arrayType.base))));
//
//            String addressRegister = allocateVirtualRegister();
//            int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
//            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(addressRegister)));
//            String temp = allocateVirtualRegister();
//            addInstruction(new IRCalculate(IRCalculate.Operator.SUB, parseType(arrayType), array, new IRConstant(constant8Index), new IRVirtualRegister(temp)));
//            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp)));
//            String sizeRegister0 = allocateVirtualRegister();
//            addInstruction(new IRLoad(new IRPointerType(IRType.getUnsignedLongType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(sizeRegister0)));
//            String sizeRegister1 = allocateVirtualRegister();
//            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister0), new IRVirtualRegister(sizeRegister1)));
//            String sizeRegister = allocateVirtualRegister();
//            int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
//            addInstruction(new IRCalculate(IRCalculate.Operator.SUB, IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister1), new IRConstant(constant16Index), new IRVirtualRegister(sizeRegister)));
//            String lengthRegister = allocateVirtualRegister();
//            addInstruction(new IRCalculate(IRCalculate.Operator.DIV, IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister), new IRConstant(constantTypeSizeIndex), new IRVirtualRegister(lengthRegister)));
//            String temp2 = allocateVirtualRegister();
//            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, parseType(arrayType), array, new IRConstant(constant16Index), new IRVirtualRegister(temp2)));
//            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp2)));
//
//            String countRegister = allocateVirtualRegister();
//            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(countRegister)));
//            int constant0Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
//            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRConstant(constant0Index)));
//
//            IRBasicBlock conditionBlock = createBasicBlock();
//            String temp3 = allocateVirtualRegister();
//            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp3)));
//            IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.GreaterEqual, new IRVirtualRegister(temp3), new IRVirtualRegister(lengthRegister), null);
//            addInstruction(irConditionalJump);
//            createBasicBlock();
//
//            String temp4 = allocateVirtualRegister();
//            addInstruction(new IRLoad(new IRPointerType(new IRPointerType(IRType.getVoidType())), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp4)));
//            String elementRegister = allocateVirtualRegister();
//            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp4), new IRVirtualRegister(elementRegister)));
//            release(new IRVirtualRegister(elementRegister), arrayType.base);
//
//            String temp5 = allocateVirtualRegister();
//            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp5)));
//            String temp6 = allocateVirtualRegister();
//            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, IRType.getUnsignedLongType(), new IRVirtualRegister(temp5), new IRConstant(constantTypeSizeIndex), new IRVirtualRegister(temp6)));
//            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp6)));
//            String temp7 = allocateVirtualRegister();
//            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp7)));
//            String temp8 = allocateVirtualRegister();
//            addInstruction(new IRIncrease(IRType.getUnsignedLongType(), new IRVirtualRegister(temp7), new IRVirtualRegister(temp8)));
//            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp8)));
//            addInstruction(new IRGoto(conditionBlock.name));
//            IRBasicBlock end = createBasicBlock();
//            irConditionalJump.target = end.name;
        }

//        addInstruction(new IRFree(array));
    }

    private IRCondition parseRelationOperator(Tokens.Operator _operator) {
        return switch (_operator) {
            case Equal -> IRCondition.Equal;
            case NotEqual -> IRCondition.NotEqual;
            case Greater -> IRCondition.Greater;
            case GreaterEqual -> IRCondition.GreaterEqual;
            case Less -> IRCondition.Less;
            case LessEqual -> IRCondition.LessEqual;
            default -> throw new RuntimeException("Invalid relation operator");
        };
    }

    private IRCondition negatesCondition(IRCondition condition) {
        return switch (condition) {
            case Equal -> IRCondition.NotEqual;
            case NotEqual -> IRCondition.Equal;
            case Greater -> IRCondition.LessEqual;
            case GreaterEqual -> IRCondition.Less;
            case Less -> IRCondition.GreaterEqual;
            case LessEqual -> IRCondition.Greater;
            case IfTrue -> IRCondition.IfFalse;
            case IfFalse -> IRCondition.IfTrue;
        };
    }
    /*
    private String allocateVirtualRegister() {
        return String.valueOf(registersCount++);
    }

    private IRControlFlowGraph createControlFlowGraph() {
        IRControlFlowGraph cfg = new IRControlFlowGraph();
        this.label2BasicBlock.put(cfg, new HashMap<>());
        this.label2LoopBegin.put(cfg, new HashMap<>());
        this.label2LoopEnd.put(cfg, new HashMap<>());
        this.abstractLoop2VLabel.put(cfg, new HashMap<>());
        return cfg;
    }

    private IRBasicBlock createBasicBlock() {
        return createBasicBlock("basicBlock_" + (this.currentCFG.basicBlocks.size()));
    }

    private IRBasicBlock createBasicBlock(String name) {
        IRBasicBlock basicBlock = new IRBasicBlock(name);
        this.currentCFG.addBasicBlock(basicBlock);
        return basicBlock;
    }

    private void addInstruction(IRInstruction instruction) {
        this.currentCFG.basicBlocks.values().stream().toList().getLast().instructions.add(instruction);
    }

    @Override
    public Object visit(LCAstNode node, Object additional) {
        if (node instanceof LCStatement statement) {
            if (statement.labels != null && !statement.labels.isEmpty()) {
                var basicBlock = createBasicBlock();
                for (String label : statement.labels)
                    this.label2BasicBlock.get(currentCFG).put(label, basicBlock.name);
            }
        }
        node.accept(this, additional);
        return null;
    }

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        this.ast = ast;

        var initCFG = createControlFlowGraph();
        currentCFG = initCFG;
        createBasicBlock("entry");
        this.module.putFunction(new IRFunction(IRType.getVoidType(), "<init>", 0, new IRField[0], initCFG));

        super.visitAst(ast, additional);

        this.currentCFG = initCFG;
        createBasicBlock("<init_string_constants>");
        this.stringConstantInitInvocations.forEach(this::addInstruction);
        createBasicBlock("<retain_string_constants>");
        this.stringConstant2GlobalDataName.values().forEach(globalDataName -> retain(new IRMacro("global_data_address", new String[]{globalDataName}), SystemTypes.String_Type));
        createBasicBlock();
        this.objectStaticInitInvocations.forEach(this::addInstruction);
        createBasicBlock();
        addInstruction(new IRReturn());

        var mainCFG = createControlFlowGraph();
        this.currentCFG = mainCFG;
        createBasicBlock("entry");
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{"<init>"}), new IRType[0], new IROperand[0], null));
        int constantNullptrIndex = this.module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), null));
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{ast.mainMethod.getFullName()}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{new IRConstant(constantNullptrIndex)}, null));
        addInstruction(new IRReturn());
        this.module.putFunction(new IRFunction(IRType.getVoidType(), "main", 0, new IRField[0], mainCFG));

        module.functions.values().forEach(function -> {
            function.controlFlowGraph.basicBlocks.values().forEach(basicBlock -> basicBlock.instructions.forEach(instruction -> {
                if (instruction instanceof IRGoto irGoto) {
                    if (irGoto.target.startsWith("<__label__>")) {
                        irGoto.target = this.label2BasicBlock.get(function.controlFlowGraph).get(irGoto.target.substring("<__label__>".length()));
                    } else if (irGoto.target.startsWith("<__loop_begin__>")) {
                        irGoto.target = this.label2LoopBegin.get(function.controlFlowGraph).get(irGoto.target.substring("<__loop_begin__>".length()));
                    } else if (irGoto.target.startsWith("<__loop_end__>")) {
                        irGoto.target = this.label2LoopEnd.get(function.controlFlowGraph).get(irGoto.target.substring("<__loop_end__>".length()));
                    }
                } else if (instruction instanceof IRConditionalJump irConditionalJump) {
                    if (irConditionalJump.target.startsWith("<__label__>")) {
                        irConditionalJump.target = this.label2BasicBlock.get(function.controlFlowGraph).get(irConditionalJump.target.substring("<__label__>".length()));
                    } else if (irConditionalJump.target.startsWith("<__loop_begin__>")) {
                        irConditionalJump.target = this.label2LoopBegin.get(function.controlFlowGraph).get(irConditionalJump.target.substring("<__loop_begin__>".length()));
                    } else if (irConditionalJump.target.startsWith("<__loop_end__>")) {
                        irConditionalJump.target = this.label2LoopEnd.get(function.controlFlowGraph).get(irConditionalJump.target.substring("<__loop_end__>".length()));
                    }
                }
            }));
            List<IRBasicBlock> toRemoveBasicBlocks;
            do {
                function.controlFlowGraph.buildEdges();
                toRemoveBasicBlocks = new LinkedList<>(function.controlFlowGraph.basicBlocks.values().stream().filter(basicBlock -> !"entry".equals(basicBlock.name) && function.controlFlowGraph.inEdges.get(basicBlock).isEmpty()).toList());
                toRemoveBasicBlocks.forEach(function.controlFlowGraph::removeBasicBlock);
            } while (!toRemoveBasicBlocks.isEmpty());
        });
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        this.createClassInstance(lcClassDeclaration);

        var lastInitCFG = this.initCFG;
        var lastInitFields = this.initFields;
        var lastInitVariableName2FieldName = this.initVariableName2FieldName;
        var lastInitCountOfSameNameVariables = this.initCountOfSameNameVariables;
        var lastStaticInitCFG = this.staticInitCFG;
        var lastStaticInitFields = this.staticInitFields;
        var lastStaticInitVariableName2FieldName = this.staticInitVariableName2FieldName;
        var lastStaticInitCountOfSameNameVariables = this.staticInitCountOfSameNameVariables;

        this.initCFG = createControlFlowGraph();
        this.initFields = new ArrayList<>();
        this.initVariableName2FieldName = new HashMap<>();
        this.initCountOfSameNameVariables = new HashMap<>();
        this.staticInitCFG = createControlFlowGraph();
        this.staticInitFields = new ArrayList<>();
        this.staticInitVariableName2FieldName = new HashMap<>();
        this.staticInitCountOfSameNameVariables = new HashMap<>();

        this.initFields.add(new IRField("<this_instance>", new IRPointerType(IRType.getVoidType())));

        List<IRField> fields = new ArrayList<>();

        fields.add(new IRField("<class_ptr>", new IRPointerType(IRType.getVoidType())));
        fields.add(new IRField("<reference_count>", IRType.getUnsignedLongType()));

        for (VariableSymbol variableSymbol : lcClassDeclaration.symbol.getAllProperties())
            fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));

        IRStructure irStructure = new IRStructure(lcClassDeclaration.getFullName(), fields.toArray(new IRField[0]));
        this.module.putStructure(irStructure);

        this.currentCFG = this.initCFG;
        createBasicBlock("entry");
        if (lcClassDeclaration.symbol.extended != null) {
            this.getThisInstance();
            IROperand thisInstance = operandStack.pop();
            addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{lcClassDeclaration.symbol.extended.getFullName() + ".<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{thisInstance}, null));
        }
        initObjectHead(lcClassDeclaration.getFullName());

        this.currentCFG = this.staticInitCFG;
        createBasicBlock("entry");

        super.visitClassDeclaration(lcClassDeclaration, additional);

        this.currentCFG = initCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.currentCFG = staticInitCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.module.putFunction(new IRFunction(IRType.getVoidType(), lcClassDeclaration.getFullName() + ".<__init__>()V", 1, this.initFields.toArray(new IRField[0]), this.initCFG));
        String staticInitFunctionName = lcClassDeclaration.getFullName() + ".<__static_init__>()V";
        this.module.putFunction(new IRFunction(IRType.getVoidType(), staticInitFunctionName, 0, this.staticInitFields.toArray(new IRField[0]), this.staticInitCFG));

        if (lcClassDeclaration.symbol.destructor == null) {
            IRBasicBlock deinitBasicBlock = new IRBasicBlock("entry");
            deinitBasicBlock.instructions.add(new IRReturn());
            IRControlFlowGraph deinitCFG = createControlFlowGraph();
            deinitCFG.addBasicBlock(deinitBasicBlock);
            this.module.putFunction(new IRFunction(IRType.getVoidType(), lcClassDeclaration.getFullName() + ".<deinit>()V", 1, new IRField[]{new IRField("<this_instance>", new IRPointerType(IRType.getVoidType()))}, deinitCFG));
        }

        this.objectStaticInitInvocations.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{staticInitFunctionName}), new IRType[0], new IROperand[0], null));

        this.initCFG = lastInitCFG;
        this.initFields = lastInitFields;
        this.initVariableName2FieldName = lastInitVariableName2FieldName;
        this.initCountOfSameNameVariables = lastInitCountOfSameNameVariables;
        this.staticInitCFG = lastStaticInitCFG;
        this.staticInitFields = lastStaticInitFields;
        this.staticInitVariableName2FieldName = lastStaticInitVariableName2FieldName;
        this.staticInitCountOfSameNameVariables = lastStaticInitCountOfSameNameVariables;
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        this.createClassInstance(lcInterfaceDeclaration);

        super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);

        List<String> keys = new ArrayList<>(lcInterfaceDeclaration.symbol.methods.stream().map(MethodSymbol::getSimpleName).toList());
        keys.add("<deinit>()V");
        this.module.name2ITableKeys.put(lcInterfaceDeclaration.getFullName(), keys);

        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        this.createClassInstance(lcEnumDeclaration);

        var lastInitCFG = this.initCFG;
        var lastInitFields = this.initFields;
        var lastInitVariableName2FieldName = this.initVariableName2FieldName;
        var lastInitCountOfSameNameVariables = this.initCountOfSameNameVariables;
        var lastStaticInitCFG = this.staticInitCFG;
        var lastStaticInitFields = this.staticInitFields;
        var lastStaticInitVariableName2FieldName = this.staticInitVariableName2FieldName;
        var lastStaticInitCountOfSameNameVariables = this.staticInitCountOfSameNameVariables;

        this.initCFG = createControlFlowGraph();
        this.initFields = new ArrayList<>();
        this.initVariableName2FieldName = new HashMap<>();
        this.initCountOfSameNameVariables = new HashMap<>();
        this.staticInitCFG = createControlFlowGraph();
        this.staticInitFields = new ArrayList<>();
        this.staticInitVariableName2FieldName = new HashMap<>();
        this.staticInitCountOfSameNameVariables = new HashMap<>();

        this.initCFG = createControlFlowGraph();
        this.staticInitCFG = createControlFlowGraph();

        this.initFields.add(new IRField("<this_instance>", new IRPointerType(IRType.getVoidType())));

        List<IRField> fields = new ArrayList<>();
        fields.add(new IRField("<class_ptr>", new IRPointerType(IRType.getVoidType())));
        fields.add(new IRField("<reference_count>", IRType.getUnsignedLongType()));

        for (VariableSymbol variableSymbol : ((LCClassDeclaration) Objects.requireNonNull(getAST(lcEnumDeclaration).getObjectDeclaration("l.lang.Enum"))).symbol.getAllProperties())
            if (!LCFlags.hasStatic(variableSymbol.flags))
                fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));
        for (VariableSymbol variableSymbol : lcEnumDeclaration.symbol.properties)
            if (!LCFlags.hasStatic(variableSymbol.flags))
                fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));

        IRStructure irStructure = new IRStructure(lcEnumDeclaration.getFullName(), fields.toArray(new IRField[0]));
        this.module.putStructure(irStructure);

        this.currentCFG = this.initCFG;
        createBasicBlock("entry");
        this.getThisInstance();
        IROperand thisInstance = operandStack.pop();
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{"l.lang.Enum.<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{thisInstance}, null));
        initObjectHead(lcEnumDeclaration.getFullName());

        this.currentCFG = this.staticInitCFG;
        createBasicBlock("entry");

        for (LCAnnotation lcAnnotation : lcEnumDeclaration.annotations) {
            this.visitAnnotation(lcAnnotation, additional);
        }
        for (LCTypeParameter lcTypeParameter : lcEnumDeclaration.typeParameters) {
            this.visitTypeParameter(lcTypeParameter, additional);
        }
        for (LCTypeReferenceExpression lcTypeReferenceExpression : lcEnumDeclaration.implementedInterfaces) {
            this.visitTypeReferenceExpression(lcTypeReferenceExpression, additional);
        }
        if (lcEnumDeclaration.delegated != null) {
            this.visit(lcEnumDeclaration.delegated, additional);
        }
        this.inStaticInit = true;
        for (LCEnumDeclaration.LCEnumFieldDeclaration field : lcEnumDeclaration.fields)
            this.visitEnumFieldDeclaration(field, additional);
        this.inStaticInit = false;
        this.visitBlock(lcEnumDeclaration.body, additional);

        this.currentCFG = initCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.currentCFG = staticInitCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.module.putFunction(new IRFunction(IRType.getVoidType(), lcEnumDeclaration.getFullName() + ".<__init__>()V", 1, this.initFields.toArray(new IRField[0]), this.initCFG));
        String staticInitFunctionName = lcEnumDeclaration.getFullName() + ".<__static_init__>()V";
        this.module.putFunction(new IRFunction(IRType.getVoidType(), staticInitFunctionName, 0, this.staticInitFields.toArray(new IRField[0]), this.staticInitCFG));

        if (lcEnumDeclaration.symbol.destructor == null) {
            IRBasicBlock deinitBasicBlock = new IRBasicBlock("entry");
            deinitBasicBlock.instructions.add(new IRReturn());
            IRControlFlowGraph deinitCFG = createControlFlowGraph();
            deinitCFG.addBasicBlock(deinitBasicBlock);
            this.module.putFunction(new IRFunction(IRType.getVoidType(), lcEnumDeclaration.getFullName() + ".<deinit>()V", 1, new IRField[]{new IRField("<this_instance>", new IRPointerType(IRType.getVoidType()))}, deinitCFG));
        }

        this.objectStaticInitInvocations.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{staticInitFunctionName}), new IRType[0], new IROperand[0], null));

        this.initCFG = lastInitCFG;
        this.initFields = lastInitFields;
        this.initVariableName2FieldName = lastInitVariableName2FieldName;
        this.initCountOfSameNameVariables = lastInitCountOfSameNameVariables;
        this.staticInitCFG = lastStaticInitCFG;
        this.staticInitFields = lastStaticInitFields;
        this.staticInitVariableName2FieldName = lastStaticInitVariableName2FieldName;
        this.staticInitCountOfSameNameVariables = lastStaticInitCountOfSameNameVariables;
        return null;
    }

    @Override
    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object additional) {
        String enumName = lcEnumFieldDeclaration.symbol.enumSymbol.getFullName();
        String place = allocateVirtualRegister();
        addInstruction(new IRMalloc(new IRMacro("structure_length", new String[]{enumName}), new IRVirtualRegister(place)));
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{enumName + ".<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{new IRVirtualRegister(place)}, null));

        IRType[] operandTypes = new IRType[lcEnumFieldDeclaration.arguments.size() + 1];
        IROperand[] operands = new IROperand[lcEnumFieldDeclaration.arguments.size() + 1];
        operandTypes[0] = new IRPointerType(IRType.getVoidType());
        operands[0] = new IRVirtualRegister(place);
        for (int i = 0; i < lcEnumFieldDeclaration.arguments.size(); i++) {
            operandTypes[i + 1] = parseType(lcEnumFieldDeclaration.arguments.get(i).theType);
            this.visit(lcEnumFieldDeclaration.arguments.get(i), additional);
            operands[i + 1] = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        }
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{lcEnumFieldDeclaration.symbol.constructor.getFullName()}), operandTypes, operands, null));

        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        this.createClassInstance(lcRecordDeclaration);

        var lastInitCFG = this.initCFG;
        var lastInitFields = this.initFields;
        var lastInitVariableName2FieldName = this.initVariableName2FieldName;
        var lastInitCountOfSameNameVariables = this.initCountOfSameNameVariables;
        var lastStaticInitCFG = this.staticInitCFG;
        var lastStaticInitFields = this.staticInitFields;
        var lastStaticInitVariableName2FieldName = this.staticInitVariableName2FieldName;
        var lastStaticInitCountOfSameNameVariables = this.staticInitCountOfSameNameVariables;

        this.initCFG = createControlFlowGraph();
        this.initFields = new ArrayList<>();
        this.initVariableName2FieldName = new HashMap<>();
        this.initCountOfSameNameVariables = new HashMap<>();
        this.staticInitCFG = createControlFlowGraph();
        this.staticInitFields = new ArrayList<>();
        this.staticInitVariableName2FieldName = new HashMap<>();
        this.staticInitCountOfSameNameVariables = new HashMap<>();

        this.initCFG = createControlFlowGraph();
        this.staticInitCFG = createControlFlowGraph();

        this.initFields.add(new IRField("<this_instance>", new IRPointerType(IRType.getVoidType())));

        List<IRField> fields = new ArrayList<>();

        fields.add(new IRField("<class_ptr>", new IRPointerType(IRType.getVoidType())));
        fields.add(new IRField("<reference_count>", IRType.getUnsignedLongType()));

        for (VariableSymbol variableSymbol : ((LCClassDeclaration) Objects.requireNonNull(getAST(lcRecordDeclaration).getObjectDeclaration("l.lang.Record"))).symbol.getAllProperties())
            if (!LCFlags.hasStatic(variableSymbol.flags))
                fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));
        for (VariableSymbol variableSymbol : lcRecordDeclaration.symbol.fields)
            fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));
        for (VariableSymbol variableSymbol : lcRecordDeclaration.symbol.properties)
            if (!LCFlags.hasStatic(variableSymbol.flags))
                fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));

        IRStructure irStructure = new IRStructure(lcRecordDeclaration.getFullName(), fields.toArray(new IRField[0]));
        this.module.putStructure(irStructure);

        this.currentCFG = this.initCFG;
        createBasicBlock("entry");
        this.getThisInstance();
        IROperand thisInstance = operandStack.pop();
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{"l.lang.Record.<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{thisInstance}, null));
        initObjectHead(lcRecordDeclaration.getFullName());

        this.currentCFG = this.staticInitCFG;
        createBasicBlock("entry");

        super.visitRecordDeclaration(lcRecordDeclaration, additional);

        this.currentCFG = initCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.currentCFG = staticInitCFG;
        createBasicBlock();
        addInstruction(new IRReturn());
        this.module.putFunction(new IRFunction(IRType.getVoidType(), lcRecordDeclaration.getFullName() + ".<__init__>()V", 1, this.initFields.toArray(new IRField[0]), this.initCFG));
        String staticInitFunctionName = lcRecordDeclaration.getFullName() + ".<__static_init__>()V";
        this.module.putFunction(new IRFunction(IRType.getVoidType(), staticInitFunctionName, 0, this.staticInitFields.toArray(new IRField[0]), this.staticInitCFG));

        if (lcRecordDeclaration.symbol.destructor == null) {
            IRBasicBlock deinitBasicBlock = new IRBasicBlock("entry");
            deinitBasicBlock.instructions.add(new IRReturn());
            IRControlFlowGraph deinitCFG = createControlFlowGraph();
            deinitCFG.addBasicBlock(deinitBasicBlock);
            this.module.putFunction(new IRFunction(IRType.getVoidType(), lcRecordDeclaration.getFullName() + ".<deinit>()V", 1, new IRField[]{new IRField("<this_instance>", new IRPointerType(IRType.getVoidType()))}, deinitCFG));
        }

        this.objectStaticInitInvocations.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{staticInitFunctionName}), new IRType[0], new IROperand[0], null));

        this.initCFG = lastInitCFG;
        this.initFields = lastInitFields;
        this.initVariableName2FieldName = lastInitVariableName2FieldName;
        this.initCountOfSameNameVariables = lastInitCountOfSameNameVariables;
        this.staticInitCFG = lastStaticInitCFG;
        this.staticInitFields = lastStaticInitFields;
        this.staticInitVariableName2FieldName = lastStaticInitVariableName2FieldName;
        this.staticInitCountOfSameNameVariables = lastStaticInitCountOfSameNameVariables;
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (LCFlags.hasAbstract(lcMethodDeclaration.modifier.flags)) return null;
        if (LCFlags.hasExtern(lcMethodDeclaration.modifier.flags)) {
            int argumentsCount = lcMethodDeclaration.parameterList.parameters.size();

            List<IRField> fields = new ArrayList<>();
            boolean hasStatic = LCFlags.hasStatic(lcMethodDeclaration.modifier.flags);
            if (!hasStatic) {
                argumentsCount++;
                fields.add(new IRField("<this_instance>", new IRPointerType(IRType.getVoidType())));
            }
            for (int i = 0; i < lcMethodDeclaration.parameterList.parameters.size(); i++) {
                VariableSymbol variableSymbol = lcMethodDeclaration.parameterList.parameters.get(i).symbol;
                fields.add(new IRField(variableSymbol.name, parseType(variableSymbol.theType)));
            }
            String externFunctionName = null;
            for (String attribute : lcMethodDeclaration.modifier.attributes) {
                if (attribute.startsWith("extern_name(")) {
                    externFunctionName = attribute.substring(12, attribute.length() - 1);
                    break;
                }
            }
            if (externFunctionName == null) {
                IRFunction irFunction = new IRFunction(List.of("extern"), parseType(lcMethodDeclaration.returnType), lcMethodDeclaration.symbol.getFullName(), argumentsCount, fields.toArray(new IRField[0]), null);
                this.module.putFunction(irFunction);
            } else {
                IRFunction irFunction = new IRFunction(List.of("extern"), parseType(lcMethodDeclaration.returnType), externFunctionName, argumentsCount, fields.toArray(new IRField[0]), null);
                this.module.putFunction(irFunction);

                IRControlFlowGraph cfg = createControlFlowGraph();
                IRBasicBlock entry = new IRBasicBlock("entry");
                String target;
                if (lcMethodDeclaration.returnType.equals(SystemTypes.VOID)) {
                    target = null;
                } else {
                    target = "tmp";
                }
                List<IRType> types = new ArrayList<>();
                List<IROperand> operands = new ArrayList<>();
                for (IRField field : fields) {
                    types.add(field.type);
                    operands.add(new IRMacro("field_address", new String[]{field.name}));
                }
                entry.instructions.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{externFunctionName}), types.toArray(new IRType[0]), operands.toArray(new IROperand[0]), new IRVirtualRegister(target)));
                entry.instructions.add(new IRReturn(target == null ? null : new IRVirtualRegister(target)));
                IRFunction wrapper = new IRFunction(parseType(lcMethodDeclaration.returnType), lcMethodDeclaration.symbol.getFullName(), argumentsCount, fields.toArray(new IRField[0]), cfg);
                this.module.putFunction(wrapper);
            }
            return null;
        } else {

            this.registersCount = 0;

            this.currentCFG = createControlFlowGraph();

            int argumentsCount = lcMethodDeclaration.parameterList.parameters.size();

            List<IRField> fields = new ArrayList<>();
            boolean hasStatic = LCFlags.hasStatic(lcMethodDeclaration.modifier.flags);
            if (!hasStatic) {
                argumentsCount++;
                fields.add(new IRField("<this_instance>", new IRPointerType(IRType.getVoidType())));
            }
            Map<String, Long> countOfSameNameVariables = new HashMap<>();
            for (int i = 0; i < lcMethodDeclaration.symbol.vars.size(); i++) {
                VariableSymbol variableSymbol = lcMethodDeclaration.symbol.vars.get(i);
                long count = countOfSameNameVariables.getOrDefault(variableSymbol.name, 0L);
                String name = variableSymbol.name + "_" + count;
                countOfSameNameVariables.put(variableSymbol.name, count + 1);
                fields.add(new IRField(name, parseType(variableSymbol.theType)));
                Stack<String> stack = this.variableName2FieldName.getOrDefault(variableSymbol.name, new Stack<>());
                if (i < (hasStatic ? argumentsCount : argumentsCount - 1)) stack.push(name);
                this.variableName2FieldName.put(variableSymbol.name, stack);
            }

            createBasicBlock("entry");
            createBasicBlock();
            if (lcMethodDeclaration.body != null) {
                this.visitBlock(lcMethodDeclaration.body, additional);
            }

            IRFunction irFunction = new IRFunction(parseType(lcMethodDeclaration.returnType), lcMethodDeclaration.symbol.getFullName(), argumentsCount, fields.toArray(new IRField[0]), this.currentCFG);
            this.module.putFunction(irFunction);

            this.variableName2FieldName.clear();

            return null;
        }
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        IRType type = parseType(lcVariableDeclaration.theType);
        if (LCFlags.hasStatic(lcVariableDeclaration.modifier.flags)) {
            String name = lcVariableDeclaration.symbol.objectSymbol.getFullName() + "." + lcVariableDeclaration.name;
            int constantTypeLengthIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(type)));
            module.globalDataSection.add(new IRGlobalDataSection.GlobalData(name, new IRConstant(constantTypeLengthIndex)));
            if (lcVariableDeclaration.init != null) {
                IRControlFlowGraph lastCFG = this.currentCFG;
                this.currentCFG = this.staticInitCFG;
                createBasicBlock();

                this.visit(lcVariableDeclaration.init, additional);
                IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                addInstruction(new IRStore(type, new IRMacro("global_data_address", new String[]{name}), result));

                this.currentCFG = lastCFG;
            }
        } else {
            boolean inMethod = this.getEnclosingMethodDeclaration(lcVariableDeclaration) != null;
            IRControlFlowGraph lastCFG = this.currentCFG;
            boolean isField = !inMethod && !inInit && !inStaticInit;
            if (!isField) {
                Stack<String> stack = this.variableName2FieldName.get(lcVariableDeclaration.name);
                String name = lcVariableDeclaration.name + "_" + stack.size();
                stack.push(name);
            } else {
                this.currentCFG = this.initCFG;
            }
            if (lcVariableDeclaration.init != null) {
                this.visit(lcVariableDeclaration.init, additional);
                IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                IROperand address;
                if (isField) {
                    this.getThisInstance();
                    IROperand op = operandStack.pop();
                    address = new IRMacro("field_address", new String[]{lcVariableDeclaration.symbol.objectSymbol.getFullName(), lcVariableDeclaration.name}, new IROperand[]{op});
                } else {
                    address = new IRMacro("field_address", new String[]{variableName2FieldName.get(lcVariableDeclaration.name).peek()});
                }
                addInstruction(new IRStore(type, address, result));
            }
            this.currentCFG = lastCFG;
        }
        return null;
    }

    @Override
    public Object visitInit(LCInit lcInit, Object additional) {
        Map<String, Stack<String>> map = this.variableName2FieldName;
        Map<String, Long> countOfSameNameVariables;
        List<IRField> fields;
        if (lcInit.isStatic) {
            this.variableName2FieldName = this.staticInitVariableName2FieldName;
            countOfSameNameVariables = this.staticInitCountOfSameNameVariables;
            fields = this.staticInitFields;
        } else {
            this.variableName2FieldName = this.initVariableName2FieldName;
            countOfSameNameVariables = this.initCountOfSameNameVariables;
            fields = this.initFields;
        }
        for (VariableSymbol variableSymbol : lcInit.vars) {
            long count = countOfSameNameVariables.getOrDefault(variableSymbol.name, 0L);
            String name = variableSymbol.name + "_" + count;
            countOfSameNameVariables.put(variableSymbol.name, count + 1);
            fields.add(new IRField(name, parseType(variableSymbol.theType)));
            this.variableName2FieldName.putIfAbsent(variableSymbol.name, new Stack<>());
        }

        IRControlFlowGraph lastCFG = this.currentCFG;
        if (lcInit.isStatic) {
            this.currentCFG = this.staticInitCFG;
            inStaticInit = true;
            super.visitInit(lcInit, additional);
            inStaticInit = false;
        } else {
            this.currentCFG = this.initCFG;
            inInit = true;
            super.visitInit(lcInit, additional);
            inInit = false;
        }
        this.currentCFG = lastCFG;
        this.variableName2FieldName = map;
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        this.visit(lcIf.condition, additional);
        IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, result, null);
        addInstruction(irConditionalJump);

        createBasicBlock();
        this.visit(lcIf.then, additional);
        IROperand thenResult = operandStack.isEmpty() ? null : operandStack.pop();

        IRBasicBlock next;
        if (lcIf._else != null) {
//            IRType resultType;
//            String resultRegister;
//            if (lcIf.theType.equals(SystemTypes.VOID)) {
//                resultType = null;
//                resultRegister = null;
//            } else {
//                resultType = parseType(lcIf.theType);
//                resultRegister = allocateVirtualRegister();
//                addInstruction(new IRPush(resultType, thenResult));
//            }
            IRGoto irGoto = new IRGoto(null);
            addInstruction(irGoto);
            next = createBasicBlock();

            this.visit(lcIf._else, additional);
//            if (resultType != null) {
//                IROperand elseResult = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
//                addInstruction(new IRPush(resultType, elseResult));
//            }

            irGoto.target = createBasicBlock().name;
//            if (resultType != null) {
//                addInstruction(new IRPop(resultType, new IRVirtualRegister(resultRegister)));
//                operandStack.push(new IRVirtualRegister(resultRegister));
//            }
        } else {
            next = createBasicBlock();
        }
        irConditionalJump.target = next.name;

        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object additional) {
        String vLabel = String.format("<loop_%d>", this.abstractLoop2VLabel.get(currentCFG).size());
        this.abstractLoop2VLabel.get(currentCFG).put(lcWhile, vLabel);

        IRBasicBlock begin = createBasicBlock();
        this.visit(lcWhile.condition, additional);
        IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, result, null);
        addInstruction(irConditionalJump);

        createBasicBlock();
        this.visit(lcWhile.body, additional);
        if (!operandStack.isEmpty()) operandStack.pop();

        addInstruction(new IRGoto(begin.name));

        var end = createBasicBlock();
        irConditionalJump.target = end.name;

        for (var label : lcWhile.labels) {
            this.label2LoopBegin.get(currentCFG).put(label, begin.name);
            this.label2LoopEnd.get(currentCFG).put(label, end.name);
        }
        this.label2LoopBegin.get(currentCFG).put(vLabel, begin.name);
        this.label2LoopEnd.get(currentCFG).put(vLabel, end.name);
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        String vLabel = String.format("<loop_%d>", this.abstractLoop2VLabel.get(currentCFG).size());
        this.abstractLoop2VLabel.get(currentCFG).put(lcDoWhile, vLabel);

        IRBasicBlock begin = createBasicBlock();
        this.visit(lcDoWhile.body, additional);

        this.visit(lcDoWhile.condition, additional);
        IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        addInstruction(new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfTrue, result, begin.name));

        var end = createBasicBlock();

        for (var label : lcDoWhile.labels) {
            this.label2LoopBegin.get(currentCFG).put(label, begin.name);
            this.label2LoopEnd.get(currentCFG).put(label, end.name);
        }
        this.label2LoopBegin.get(currentCFG).put(vLabel, begin.name);
        this.label2LoopEnd.get(currentCFG).put(vLabel, end.name);
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object additional) {
        String vLabel = String.format("<loop_%d>", this.abstractLoop2VLabel.get(currentCFG).size());
        this.abstractLoop2VLabel.get(currentCFG).put(lcFor, vLabel);
        if (lcFor.init != null) {
            this.visit(lcFor.init, additional);
        }
        IRBasicBlock condition = createBasicBlock();
        IRConditionalJump irConditionalJump;
        if (lcFor.condition != null) {
            this.visit(lcFor.condition, additional);
            IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, result, null);
            addInstruction(irConditionalJump);
            createBasicBlock();
        } else {
            irConditionalJump = null;
        }

        this.visit(lcFor.body, additional);

        if (lcFor.increment != null) {
            this.visit(lcFor.increment, additional);
            if (!operandStack.isEmpty()) operandStack.pop();
        }
        addInstruction(new IRGoto(condition.name));

        IRBasicBlock end = createBasicBlock();
        List<Symbol> symbols = lcFor.scope.name2symbol.values().stream().toList();
        for (int i = symbols.size() - 1; i >= 0; i--) {
            if (symbols.get(i) instanceof VariableSymbol variableSymbol)
                variableName2FieldName.get(variableSymbol.name).pop();
        }

        if (irConditionalJump != null) {
            irConditionalJump.target = end.name;
        }
        for (var label : lcFor.labels) {
            this.label2LoopBegin.get(currentCFG).put(label, condition.name);
            this.label2LoopEnd.get(currentCFG).put(label, end.name);
        }
        this.label2LoopBegin.get(currentCFG).put(vLabel, condition.name);
        this.label2LoopEnd.get(currentCFG).put(vLabel, end.name);
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object additional) {
        String vLabel = String.format("<loop_%d>", this.abstractLoop2VLabel.get(currentCFG).size());
        this.abstractLoop2VLabel.get(currentCFG).put(lcLoop, vLabel);

        IRBasicBlock begin = createBasicBlock();
        this.visit(lcLoop.body, additional);
        addInstruction(new IRGoto(begin.name));

        var end = createBasicBlock();

        for (var label : lcLoop.labels) {
            this.label2LoopBegin.get(currentCFG).put(label, begin.name);
            this.label2LoopEnd.get(currentCFG).put(label, end.name);
        }
        this.label2LoopBegin.get(currentCFG).put(vLabel, begin.name);
        this.label2LoopEnd.get(currentCFG).put(vLabel, end.name);
        return null;
    }

    @Override
    public Object visitReturn(LCReturn lcReturn, Object additional) {
        IROperand value;
        if (lcReturn.returnedValue != null) {
            this.visit(lcReturn.returnedValue, additional);
            if (!operandStack.isEmpty()) value = operandStack.pop();
            else value = new IRConstant(-1);
            retain(value, lcReturn.returnedValue.theType);
        } else {
            value = null;
        }
        Scope scope = getEnclosingScope(lcReturn);
        while (scope != null && !(scope.node instanceof LCMethodDeclaration) && !(scope.node instanceof LCObjectDeclaration)) {
            releaseScope(scope);
            scope = scope.enclosingScope;
        }
        LCMethodDeclaration methodDeclaration = getEnclosingMethodDeclaration(lcReturn);
        if (methodDeclaration != null && !LCFlags.hasStatic(methodDeclaration.modifier.flags)) {
            getThisInstance();
            IROperand thisInstance = operandStack.pop();
            release(thisInstance, LCAstUtil.getObjectSymbol(getEnclosingObjectDeclaration(lcReturn)).theType);
        }

        addInstruction(new IRReturn(value));

        createBasicBlock();
        return null;
    }

    @Override
    public Object visitGoto(LCGoto lcGoto, Object additional) {
        addInstruction(new IRGoto("<__label__>" + lcGoto.label));
        createBasicBlock();
        return null;
    }

    @Override
    public Object visitBreak(LCBreak lcBreak, Object additional) {
        if (lcBreak.label != null) {
            addInstruction(new IRGoto("<__loop_end__>" + lcBreak.label));
        } else {
            LCAbstractLoop abstractLoop = getEnclosingLoop(lcBreak);
            addInstruction(new IRGoto("<__loop_end__>" + this.abstractLoop2VLabel.get(currentCFG).get(abstractLoop)));
        }
        createBasicBlock();
        return null;
    }

    @Override
    public Object visitContinue(LCContinue lcContinue, Object additional) {
        if (lcContinue.label != null) {
            addInstruction(new IRGoto("<__loop_begin__>" + lcContinue.label));
        } else {
            LCAbstractLoop abstractLoop = getEnclosingLoop(lcContinue);
            addInstruction(new IRGoto("<__loop_begin__>" + this.abstractLoop2VLabel.get(currentCFG).get(abstractLoop)));
        }
        createBasicBlock();
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object additional) {
        IRType[] types = new IRType[lcNative.resources.size()];
        IROperand[] resources = new IROperand[lcNative.resources.size()];
        String[] names = new String[lcNative.resources.size()];
        for (int i = 0; i < lcNative.resources.size(); i++) {
            LCNative.LCResourceForNative resource = lcNative.resources.get(i);
            types[i] = parseType(resource.resource.theType);
            this.visitResourceForNative(resource, additional);
            resources[i] = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            names[i] = resource.name;
        }
        StringBuilder builder = new StringBuilder();
        for (LCNativeSection section : lcNative.sections) builder.append(section.code);
        addInstruction(new IRAsm(builder.toString(), types, resources, names));
        return null;
    }

    @Override
    public Object visitResourceForNative(LCNative.LCResourceForNative lcResourceForNative, Object additional) {
        this.visit(lcResourceForNative.resource, additional);

        return null;
    }

    @Override
    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object additional) {
        this.visit(lcExpressionStatement.expression, additional);

        if (!operandStack.isEmpty()) {
            release(operandStack.pop(), lcExpressionStatement.expression.theType);
        }
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        for (int i = 0; i < lcBlock.statements.size(); i++) {
            LCStatement statement = lcBlock.statements.get(i);
            this.visit(statement, additional);
//            IROperand operand;
//            if (i + 1 == lcBlock.statements.size() && statement instanceof LCExpressionStatement) {
//                operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
//            } else {
//                operand = null;
//            }
            operandStack.clear();
//            if (operand != null) {
//                operandStack.push(operand);
//            }
        }
        if (!lcBlock.statements.isEmpty() && !(lcBlock.statements.getLast() instanceof LCReturn)) {
            if (this.getEnclosingMethodDeclaration(lcBlock) != null || this.getEnclosingInit(lcBlock) != null) {
                releaseScope(lcBlock.scope);
                List<Symbol> symbols = lcBlock.scope.name2symbol.values().stream().toList();
                for (int i = symbols.size() - 1; i >= 0; i--) {
                    if (symbols.get(i) instanceof VariableSymbol variableSymbol)
                        variableName2FieldName.get(variableSymbol.name).pop();
                }
            }
        }

        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        IRType operandType = parseType(lcBinary.theType);

        if (lcBinary._operator == Tokens.Operator.Dot || lcBinary._operator == Tokens.Operator.MemberAccess) {
            this.visit(lcBinary.expression1, additional);
            Type type = lcBinary.expression1.theType;
            if (type instanceof NullableType nullableType) type = nullableType.base;
            if (lcBinary._operator == Tokens.Operator.MemberAccess) {
                type = ((PointerType) type).base;
                IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                String tempRegister = allocateVirtualRegister();
                addInstruction(new IRLoad(parseType(type), operand, new IRVirtualRegister(tempRegister)));
                operandStack.push(new IRVirtualRegister(tempRegister));
            }

            if (type instanceof ArrayType arrayType && lcBinary.expression2 instanceof LCVariable lcVariable && "length".equals(lcVariable.name)) {
                IROperand array = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                arrayLength(array, arrayType.base);
            } else {
                if (lcBinary.expression2 instanceof LCMethodCall lcMethodCall && lcMethodCall.symbol.methodKind == MethodKind.Destructor && lcBinary.expression1 instanceof LCSuper lcSuper) {
                    IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                    callMethod(lcMethodCall.symbol, List.of(operand), List.of(lcSuper.theType), true);
                } else {
                    this.visit(lcBinary.expression2, additional);
                }
            }
        } else if (Token.isLogicalOperator(lcBinary._operator)) {
            int constantTrueIndex = module.constantPool.put(new IRConstantPool.Entry(operandType, true));
            int constantFalseIndex = module.constantPool.put(new IRConstantPool.Entry(operandType, false));
            String result = allocateVirtualRegister();
            if (lcBinary._operator == Tokens.Operator.And) {
                this.visit(lcBinary.expression1, additional);
                IROperand operand1 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, operand1, null);
                addInstruction(irConditionalJump);
                createBasicBlock();
                this.visit(lcBinary.expression2, additional);
                IROperand operand2 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

                int constant1Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 1));
                String tempRegister = allocateVirtualRegister();
                addInstruction(new IRStackAllocate(new IRConstant(constant1Index), new IRVirtualRegister(tempRegister)));

                IRConditionalJump irConditionalJump2 = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, operand2, null);
                addInstruction(irConditionalJump2);
                createBasicBlock();

                addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantTrueIndex)));
                IRGoto irGoto = new IRGoto(null);
                addInstruction(irGoto);
                IRBasicBlock bb = createBasicBlock();
                addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantFalseIndex)));
                irConditionalJump.target = bb.name;
                irConditionalJump2.target = bb.name;
                IRBasicBlock end = createBasicBlock();
                end.instructions.add(new IRLoad(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRVirtualRegister(result)));
                irGoto.target = end.name;
            } else if (lcBinary._operator == Tokens.Operator.Or) {
                this.visit(lcBinary.expression1, additional);
                IROperand operand1 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfTrue, operand1, null);
                addInstruction(irConditionalJump);
                createBasicBlock();
                this.visit(lcBinary.expression2, additional);
                IROperand operand2 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                int constant1Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 1));
                String tempRegister = allocateVirtualRegister();
                addInstruction(new IRStackAllocate(new IRConstant(constant1Index), new IRVirtualRegister(tempRegister)));
                IRConditionalJump irConditionalJump2 = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, operand2, null);
                addInstruction(irConditionalJump2);

                IRBasicBlock bb = createBasicBlock();
                irConditionalJump.target = bb.name;
                addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantTrueIndex)));
                IRGoto irGoto = new IRGoto(null);
                addInstruction(irGoto);
                IRBasicBlock bb2 = createBasicBlock();
                irConditionalJump2.target = bb2.name;
                addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantFalseIndex)));
                IRBasicBlock end = createBasicBlock();
                end.instructions.add(new IRLoad(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRVirtualRegister(result)));
                irGoto.target = end.name;
            }
            operandStack.push(new IRVirtualRegister(result));
        } else {
            this.visit(lcBinary.expression1, additional);
            IROperand operand1 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

            this.visit(lcBinary.expression2, additional);
            IROperand operand2 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

            if (lcBinary.methodSymbol != null) {
                callMethod(lcBinary.methodSymbol, List.of(operand1, operand2), List.of(lcBinary.expression1.theType, lcBinary.expression2.theType));
            } else if (lcBinary._operator == Tokens.Operator.Plus) {
                String resultRegister = allocateVirtualRegister();
                if (lcBinary.expression1.theType instanceof PointerType pointerType) {
                    IRType elementType = parseType(pointerType.base);
                    String tempRegister = allocateVirtualRegister();
                    int constantElementSizeIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(elementType)));
                    addInstruction(new IRCalculate(IRCalculate.Operator.MUL, IRType.getUnsignedLongType(), new IRConstant(constantElementSizeIndex), operand2, new IRVirtualRegister(tempRegister)));
                    addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(elementType), operand1, new IRVirtualRegister(tempRegister), new IRVirtualRegister(resultRegister)));
                } else {
                    addInstruction(new IRCalculate(IRCalculate.Operator.ADD, operandType, operand1, operand2, new IRVirtualRegister(resultRegister)));
                }
                operandStack.push(new IRVirtualRegister(resultRegister));
            } else if (lcBinary._operator == Tokens.Operator.Minus) {
                String resultRegister = allocateVirtualRegister();
                if (lcBinary.expression1.theType instanceof PointerType pointerType) {
                    IRType elementType = parseType(pointerType.base);
                    String tempRegister = allocateVirtualRegister();
                    int constantElementSizeIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(elementType)));
                    addInstruction(new IRCalculate(IRCalculate.Operator.MUL, IRType.getUnsignedLongType(), new IRConstant(constantElementSizeIndex), operand2, new IRVirtualRegister(tempRegister)));
                    addInstruction(new IRCalculate(IRCalculate.Operator.SUB, new IRPointerType(elementType), operand1, new IRVirtualRegister(tempRegister), new IRVirtualRegister(resultRegister)));
                } else {
                    addInstruction(new IRCalculate(IRCalculate.Operator.SUB, operandType, operand1, operand2, new IRVirtualRegister(resultRegister)));
                }
                operandStack.push(new IRVirtualRegister(resultRegister));
            } else if (Token.isRelationOperator(lcBinary._operator)) {
                String resultRegister = allocateVirtualRegister();
                addInstruction(new IRCompare(parseType(TypeUtil.getUpperBound(lcBinary.expression1.theType, lcBinary.expression2.theType)), this.parseRelationOperator(lcBinary._operator), operand1, operand2, new IRVirtualRegister(resultRegister)));
                operandStack.push(new IRVirtualRegister(resultRegister));
            } else if (Token.isArithmeticOperator(lcBinary._operator)) {
                String resultRegister = allocateVirtualRegister();
                addInstruction(new IRCalculate(false, this.parseArithmeticOperator(lcBinary._operator), operandType, operand1, operand2, new IRVirtualRegister(resultRegister)));
                operandStack.push(new IRVirtualRegister(resultRegister));
            } else if (Token.isAssignOperator(lcBinary._operator)) {
                IROperand result;
                switch (lcBinary._operator) {
                    case Assign -> {
                        retain(operand2, lcBinary.expression2.theType);
                        String tempRegister = allocateVirtualRegister();
                        addInstruction(new IRLoad(operandType, operand1, new IRVirtualRegister(tempRegister)));
                        release(new IRVirtualRegister(tempRegister), lcBinary.expression1.theType);
                        result = operand2;
                    }
                    case PlusAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.ADD, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case MinusAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.SUB, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case MultiplyAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.MUL, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case DivideAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.DIV, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case ModulusAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.MOD, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case LeftShiftArithmeticAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.SHL, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case RightShiftArithmeticAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.SHR, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case RightShiftLogicalAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.USHR, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case BitAndAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.AND, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case BitOrAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.OR, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case BitXorAssign -> {
                        IRType operand1Type = parseType(lcBinary.expression1.theType);
                        String operand1Register = allocateVirtualRegister();
                        addInstruction(new IRLoad(operand1Type, operand1, new IRVirtualRegister(operand1Register)));
                        String resultRegister = allocateVirtualRegister();
                        addInstruction(new IRCalculate(IRCalculate.Operator.XOR, operandType, new IRVirtualRegister(operand1Register), operand2, new IRVirtualRegister(resultRegister)));
                        result = new IRVirtualRegister(resultRegister);
                    }
                    case null, default -> throw new RuntimeException("Unsupported operator: " + lcBinary._operator);
                }
                addInstruction(new IRStore(operandType, operand1, result));
                operandStack.push(result);
            }
        }
        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object additional) {
        this.visit(lcUnary.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        if (lcUnary.methodSymbol != null) {
            IRType type = parseType(lcUnary.expression.theType);
            IROperand irOperand;
            if (lcUnary._operator == Tokens.Operator.Inc || lcUnary._operator == Tokens.Operator.Dec) {
                String operandRegister = allocateVirtualRegister();
                addInstruction(new IRLoad(type, operand, new IRVirtualRegister(operandRegister)));
                irOperand = new IRVirtualRegister(operandRegister);
            } else {
                irOperand = operand;
            }
            callMethod(lcUnary.methodSymbol, List.of(irOperand), List.of(lcUnary.expression.theType));
            IROperand result = SystemTypes.VOID.equals(lcUnary.methodSymbol.returnType) ? null : operandStack.pop();
            if (result != null) {
                if (lcUnary._operator == Tokens.Operator.Inc || lcUnary._operator == Tokens.Operator.Dec) {
                    addInstruction(new IRStore(type, operand, result));
                    if (lcUnary.isPrefix) operandStack.push(result);
                    else operandStack.push(irOperand);
                } else {
                    operandStack.push(result);
                }
            }
        } else if (lcUnary.isPrefix) {
            if (lcUnary._operator == Tokens.Operator.Plus) {
                operandStack.push(operand);
            } else {
                IRType type = parseType(lcUnary.expression.theType);
                String resultRegister = allocateVirtualRegister();
                if (lcUnary._operator == Tokens.Operator.Inc) {
                    String operandRegister = allocateVirtualRegister();
                    addInstruction(new IRLoad(type, operand, new IRVirtualRegister(operandRegister)));
                    addInstruction(new IRIncrease(type, new IRVirtualRegister(operandRegister), new IRVirtualRegister(resultRegister)));
                    addInstruction(new IRStore(type, operand, new IRVirtualRegister(resultRegister)));
                    operandStack.push(new IRVirtualRegister(resultRegister));
                } else if (lcUnary._operator == Tokens.Operator.Dec) {
                    String operandRegister = allocateVirtualRegister();
                    addInstruction(new IRLoad(type, operand, new IRVirtualRegister(operandRegister)));
                    addInstruction(new IRDecrease(type, new IRVirtualRegister(operandRegister), new IRVirtualRegister(resultRegister)));
                    addInstruction(new IRStore(type, operand, new IRVirtualRegister(resultRegister)));
                    operandStack.push(new IRVirtualRegister(resultRegister));
                } else {
                    if (lcUnary._operator == Tokens.Operator.Minus) {
                        addInstruction(new IRNegate(false, type, operand, new IRVirtualRegister(resultRegister)));
                    } else if (lcUnary._operator == Tokens.Operator.BitNot) {
                        addInstruction(new IRNot(false, type, operand, new IRVirtualRegister(resultRegister)));
                    }
                    operandStack.push(new IRVirtualRegister(resultRegister));
                }
            }
        } else {
            IRType type = parseType(lcUnary.expression.theType);
            String operandRegister = allocateVirtualRegister();
            String resultRegister = allocateVirtualRegister();
            addInstruction(new IRLoad(type, operand, new IRVirtualRegister(operandRegister)));
            if (lcUnary._operator == Tokens.Operator.Inc) {
                addInstruction(new IRIncrease(type, new IRVirtualRegister(operandRegister), new IRVirtualRegister(resultRegister)));
            } else if (lcUnary._operator == Tokens.Operator.Dec) {
                addInstruction(new IRDecrease(type, new IRVirtualRegister(operandRegister), new IRVirtualRegister(resultRegister)));
            } else {
                throw new IllegalArgumentException("Unknown unary operator: " + lcUnary._operator);
            }
            addInstruction(new IRStore(type, operand, new IRVirtualRegister(resultRegister)));
            operandStack.push(new IRVirtualRegister(operandRegister));
        }
        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object additional) {
        getVariable(lcVariable.symbol, lcVariable.isLeftValue);
        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object additional) {
        List<IRType> irTypes;
        List<IROperand> arguments;
        List<Type> types;
        if (LCFlags.hasStatic(lcMethodCall.symbol.flags)) {
            irTypes = new ArrayList<>(lcMethodCall.arguments.size());
            arguments = new ArrayList<>(lcMethodCall.arguments.size());
            types = new ArrayList<>(lcMethodCall.arguments.size());

            for (int i = 0; i < lcMethodCall.arguments.size(); i++) {
                LCExpression argument = lcMethodCall.arguments.get(i);
                irTypes.add(parseType(argument.theType));
                this.visit(argument, additional);
                arguments.add(operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop());
                types.add(argument.theType);
            }
        } else {
            irTypes = new ArrayList<>(lcMethodCall.arguments.size() + 1);
            arguments = new ArrayList<>(lcMethodCall.arguments.size() + 1);
            types = new ArrayList<>(lcMethodCall.arguments.size() + 1);

            if (operandStack.isEmpty()) this.getThisInstance();
            IROperand thisInstance = operandStack.pop();

            irTypes.add(new IRPointerType(IRType.getVoidType()));
            arguments.add(thisInstance);
            types.add(lcMethodCall.symbol.objectSymbol.theType);

            for (int i = 0; i < lcMethodCall.arguments.size(); i++) {
                LCExpression argument = lcMethodCall.arguments.get(i);
                irTypes.add(parseType(argument.theType));
                this.visit(argument, additional);
                arguments.add(operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop());
                types.add(argument.theType);
            }
        }
        if (lcMethodCall.expression != null) {
            this.visit(lcMethodCall.expression, additional);
            IROperand address = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            IRVirtualRegister result;
            if (!lcMethodCall.symbol.returnType.equals(SystemTypes.VOID)) {
                result = new IRVirtualRegister(allocateVirtualRegister());
            } else {
                result = null;
            }
            for (int i = 0; i < arguments.size(); i++) {
                retain(arguments.get(i), types.get(i));
            }
            addInstruction(new IRInvoke(parseType(lcMethodCall.theType), address, irTypes.toArray(new IRType[0]), arguments.toArray(new IROperand[0]), result));
            if (result != null) {
                operandStack.push(result);
            }
        } else {
            callMethod(lcMethodCall.symbol, arguments, types);
        }
        return null;
    }

    @Override
    public Object visitMalloc(LCMalloc lcMalloc, Object additional) {
        this.visit(lcMalloc.size, additional);
        IROperand size = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        String resultRegister = allocateVirtualRegister();
        addInstruction(new IRMalloc(size, new IRVirtualRegister(resultRegister)));

        operandStack.push(new IRVirtualRegister(resultRegister));
        return null;
    }

    @Override
    public Object visitFree(LCFree lcFree, Object additional) {
        this.visit(lcFree.expression, additional);
        IROperand ptr = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        addInstruction(new IRFree(ptr));

        operandStack.push(ptr);
        return null;
    }

    @Override
    public Object visitRealloc(LCRealloc lcRealloc, Object additional) {
        this.visit(lcRealloc.expression, additional);
        IROperand ptr = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        this.visit(lcRealloc.size, additional);
        IROperand size = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        String resultRegister = allocateVirtualRegister();

        addInstruction(new IRRealloc(ptr, size, new IRVirtualRegister(resultRegister)));

        operandStack.push(new IRVirtualRegister(resultRegister));
        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object additional) {
        this.visit(lcArrayAccess.base, additional);
        IROperand base = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        this.visit(lcArrayAccess.index, additional);
        IROperand index = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        IRType elementType = parseType(lcArrayAccess.theType);
        IROperand temp;
        IRConditionalJump conditionalJump1;
        IRConditionalJump conditionalJump2;
        if (lcArrayAccess.base.theType instanceof ArrayType arrayType) {
            arrayLength(base, arrayType.base);
            IROperand length = operandStack.pop();
            int constant0Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
            conditionalJump1 = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.Less, index, new IRConstant(constant0Index), "");
            addInstruction(conditionalJump1);
            createBasicBlock();
            conditionalJump2 = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.GreaterEqual, index, length, "");
            addInstruction(conditionalJump2);
            createBasicBlock();
            int constant16Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
            String tempRegister = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(elementType), base, new IRConstant(constant16Index), new IRVirtualRegister(tempRegister)));
            temp = new IRVirtualRegister(tempRegister);
        } else {
            temp = base;
            conditionalJump1 = null;
            conditionalJump2 = null;
        }
        String temp2Register = allocateVirtualRegister();
        int constantElementSizeIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(elementType)));
        addInstruction(new IRCalculate(IRCalculate.Operator.MUL, IRType.getUnsignedLongType(), new IRConstant(constantElementSizeIndex), index, new IRVirtualRegister(temp2Register)));
        String addressRegister = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(elementType), temp, new IRVirtualRegister(temp2Register), new IRVirtualRegister(addressRegister)));
        if (lcArrayAccess.isLeftValue) {
            operandStack.push(new IRVirtualRegister(addressRegister));
        } else {
            String result = allocateVirtualRegister();
            addInstruction(new IRLoad(elementType, new IRVirtualRegister(addressRegister), new IRVirtualRegister(result)));
            operandStack.push(new IRVirtualRegister(result));
        }

        if (conditionalJump1 != null) {
            IRGoto irGoto = new IRGoto(null);
            addInstruction(irGoto);
            IRBasicBlock basicBlock = createBasicBlock();
            conditionalJump1.target = basicBlock.name;
            conditionalJump2.target = basicBlock.name;
            // TODO throw ArrayIndexOutOfBoundsException
            IRBasicBlock next = createBasicBlock();
            irGoto.target = next.name;
        }
        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object additional) {
        this.getThisInstance();
        return null;
    }

    @Override
    public Object visitSuper(LCSuper lcSuper, Object additional) {
        this.getThisInstance();
        return null;
    }

    @Override
    public Object visitEmptyExpression(LCEmptyExpression lcEmptyExpression, Object additional) {
        return super.visitEmptyExpression(lcEmptyExpression, additional);
    }

    @Override
    public Object visitEmptyStatement(LCEmptyStatement lcEmptyStatement, Object additional) {
        return super.visitEmptyStatement(lcEmptyStatement, additional);
    }

    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object additional) {
        this.visit(lcGetAddress.expression, additional);
        return null;
    }

    @Override
    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object additional) {
        this.visit(lcNotNullAssert.base, additional);
        IROperand base = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        int constantNullIndex = this.module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), null));
        IRConditionalJump conditionalJump = new IRConditionalJump(new IRPointerType(IRType.getVoidType()), IRCondition.NotEqual, base, new IRConstant(constantNullIndex), "");
        addInstruction(conditionalJump);
        createBasicBlock();
        // TODO throws NullPointerException
        var end = createBasicBlock();
        conditionalJump.target = end.name;

        operandStack.push(base);
        return null;
    }

    @Override
    public Object visitAssert(LCAssert lcAssert, Object additional) {
        this.visit(lcAssert.condition, additional);
        IROperand condition = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        IRConditionalJump conditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfTrue, condition, null);
        addInstruction(conditionalJump);
        createBasicBlock();
        // TODO throws AssertionError
        var end = createBasicBlock();
        conditionalJump.target = end.name;
        return null;
    }

    @Override
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object additional) {
        int index;
        if (lcIntegerLiteral.theType.equals(SystemTypes.LONG)) {
            index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getLongType(), lcIntegerLiteral.value));
        } else {
            index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getIntType(), lcIntegerLiteral.value));
        }
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitDecimalLiteral(LCDecimalLiteral lcDecimalLiteral, Object additional) {
        int index;
        if (lcDecimalLiteral.theType.equals(SystemTypes.FLOAT)) {
            index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getFloatType(), lcDecimalLiteral.value));
        } else {
            index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getDoubleType(), lcDecimalLiteral.value));
        }
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitBooleanLiteral(LCBooleanLiteral lcBooleanLiteral, Object additional) {
        int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getBooleanType(), lcBooleanLiteral.value));
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitCharLiteral(LCCharLiteral lcCharLiteral, Object additional) {
        int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getCharType(), lcCharLiteral.value));
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitStringLiteral(LCStringLiteral lcStringLiteral, Object additional) {
        String globalDataName;
        if (this.stringConstant2GlobalDataName.containsKey(lcStringLiteral.value)) {
            globalDataName = this.stringConstant2GlobalDataName.get(lcStringLiteral.value);
        } else {
            globalDataName = "<string_" + this.stringConstant2GlobalDataName.size() + ">";
            this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(globalDataName, new IRMacro("structure_length", new String[]{SystemTypes.String_Type.name})));
            int index = this.module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), lcStringLiteral.value));
            stringConstantInitInvocations.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{SystemTypes.String_Type.name + ".<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{new IRMacro("global_data_address", new String[]{globalDataName})}, null));
            stringConstantInitInvocations.add(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{SystemTypes.String_Type.name + ".<init>(PV)V"}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRPointerType.getVoidType())}, new IROperand[]{new IRMacro("global_data_address", new String[]{globalDataName}), new IRConstant(index)}, null));
            this.stringConstant2GlobalDataName.put(lcStringLiteral.value, globalDataName);
        }
        operandStack.push(new IRMacro("global_data_address", new String[]{globalDataName}));
        return null;
    }

    @Override
    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object additional) {
        int index = this.module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), lcNullLiteral.value));
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object additional) {
        int index = this.module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), lcNullptrLiteral.value));
        operandStack.push(new IRConstant(index));
        return null;
    }

    @Override
    public Object visitSizeof(LCSizeof lcSizeof, Object additional) {
        if (SystemTypes.BYTE.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_BYTE.equals(lcSizeof.expression.theType) || SystemTypes.BOOLEAN.equals(lcSizeof.expression.theType)) {
            int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 1));
            operandStack.push(new IRConstant(index));
        } else if (SystemTypes.SHORT.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_SHORT.equals(lcSizeof.expression.theType)) {
            int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 2));
            operandStack.push(new IRConstant(index));
        } else if (SystemTypes.INT.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_INT.equals(lcSizeof.expression.theType) || SystemTypes.FLOAT.equals(lcSizeof.expression.theType) || SystemTypes.CHAR.equals(lcSizeof.expression.theType)) {
            int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 4));
            operandStack.push(new IRConstant(index));
        } else if (SystemTypes.LONG.equals(lcSizeof.expression.theType) || SystemTypes.UNSIGNED_LONG.equals(lcSizeof.expression.theType) || SystemTypes.DOUBLE.equals(lcSizeof.expression.theType) || lcSizeof.expression.theType instanceof PointerType) {
            int index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
            operandStack.push(new IRConstant(index));
        } else {
        }
        return null;
    }

    @Override
    public Object visitClone(LCClone lcClone, Object additional) {

        this.visit(lcClone.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        if (SystemTypes.isPrimitiveType(lcClone.expression.theType)) {
            operandStack.push(operand);
        } else {
            IRType type = parseType(lcClone.expression.theType);
            // TODO clone object
        }
        return null;
    }

    @Override
    public Object visitTypeof(LCTypeof lcTypeof, Object additional) {
        this.visit(lcTypeof.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        IRType stringType = parseType(SystemTypes.String_Type);

        if (lcTypeof.expression.theType instanceof PointerType || lcTypeof.expression.theType instanceof ReferenceType || lcTypeof.expression.theType instanceof ArrayType || SystemTypes.isPrimitiveType(lcTypeof.expression.theType)) {
            int index = module.constantPool.put(new IRConstantPool.Entry(stringType, lcTypeof.expression.theType.toTypeString()));
            operandStack.push(new IRConstant(index));
        }
        return null;
    }

    @Override
    public Object visitTypeCast(LCTypeCast lcTypeCast, Object additional) {
        this.visit(lcTypeCast.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        if (lcTypeCast.kind == LCTypeCast.Kind.REINTERPRET || lcTypeCast.expression.theType.equals(lcTypeCast.typeExpression.theType)) {
            operandStack.push(operand);
        } else if (lcTypeCast.kind == LCTypeCast.Kind.STATIC) {
            if ((lcTypeCast.expression.theType instanceof PointerType || SystemTypes.isPrimitiveType(lcTypeCast.expression.theType)) && (lcTypeCast.expression.theType instanceof PointerType || SystemTypes.isPrimitiveType(lcTypeCast.typeExpression.theType))) {
                IRType originalType = parseType(lcTypeCast.expression.theType);
                IRType targetType = parseType(lcTypeCast.typeExpression.theType);
                String result = allocateVirtualRegister();
                addInstruction(new IRTypeCast(parseTypeCast(lcTypeCast.expression.theType, lcTypeCast.typeExpression.theType), originalType, operand, targetType, new IRVirtualRegister(result)));
                operandStack.push(new IRVirtualRegister(result));
            } else {
                operandStack.push(operand);
            }
        } else {
            String operandClassInstanceAddressRegister = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), operand, new IRVirtualRegister(operandClassInstanceAddressRegister)));
            String classInstanceName = String.format("<class_instance %s>", lcTypeCast.typeExpression.theType.toTypeString());
            ClassSymbol classSymbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
            MethodSymbol methodSymbol = null;
            for (MethodSymbol method : classSymbol.methods) {
                if (method.name.equals("isSubClassOf")) {
                    methodSymbol = method;
                    break;
                }
            }
            String resultRegister = allocateVirtualRegister();
            IRMacro methodAddress = new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol).getFullName()});
            addInstruction(new IRInvoke(IRType.getBooleanType(), methodAddress, new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{new IRVirtualRegister(operandClassInstanceAddressRegister), new IRMacro("global_data_address", new String[]{classInstanceName})}, new IRVirtualRegister(resultRegister)));
            IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfTrue, new IRVirtualRegister(resultRegister), null);
            addInstruction(irConditionalJump);
            createBasicBlock();
            // TODO throw exception

            var end = createBasicBlock();
            irConditionalJump.target = end.name;

            operandStack.push(operand);
        }
        return null;
    }

    @Override
    public Object visitIs(LCIs lcIs, Object additional) {
        this.visit(lcIs.expression1, additional);
        IROperand operand1 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        this.visit(lcIs.expression2, additional);
        IROperand operand2 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        IRType type = parseType(TypeUtil.getUpperBound(lcIs.expression1.theType, lcIs.expression2.theType));

        int constant1Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 1));
        String tempRegister = allocateVirtualRegister();
        addInstruction(new IRStackAllocate(new IRConstant(constant1Index), new IRVirtualRegister(tempRegister)));

        int constantTrueIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getBooleanType(), true));
        int constantFalseIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getBooleanType(), false));
        IRConditionalJump irConditionalJump = new IRConditionalJump(type, IRCondition.NotEqual, operand1, operand2, null);
        addInstruction(irConditionalJump);

        createBasicBlock();
        addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantTrueIndex)));
        IRGoto irGoto = new IRGoto(null);
        addInstruction(irGoto);

        IRBasicBlock falseBlock = createBasicBlock();
        addInstruction(new IRStore(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRConstant(constantFalseIndex)));

        IRBasicBlock end = createBasicBlock();
        String result = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getBooleanType(), new IRVirtualRegister(tempRegister), new IRVirtualRegister(result)));

        irConditionalJump.target = falseBlock.name;
        irGoto.target = end.name;

        operandStack.push(new IRVirtualRegister(result));
        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object additional) {
        this.visit(lcDereference.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        if (lcDereference.isLeftValue) {
            operandStack.push(operand);
        } else {
            IRType type = parseType(lcDereference.theType);
            String result = allocateVirtualRegister();
            addInstruction(new IRLoad(type, operand, new IRVirtualRegister(result)));

            operandStack.push(new IRVirtualRegister(result));
        }
        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        String typeName = lcNewObject.theType.toTypeString();

        IROperand place;
        if (lcNewObject.place != null) {
            this.visit(lcNewObject.place, additional);
            place = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            retain(place, lcNewObject.theType);
        } else {
            String addressRegister = allocateVirtualRegister();
            addInstruction(new IRMalloc(new IRMacro("structure_length", new String[]{typeName}), new IRVirtualRegister(addressRegister)));
            place = new IRVirtualRegister(addressRegister);
            int constant8Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
            String tempRegister = allocateVirtualRegister();
            addInstruction(new IRCalculate(false, IRCalculate.Operator.ADD, new IRPointerType(IRType.getUnsignedLongType()), place, new IRConstant(constant8Index), new IRVirtualRegister(tempRegister)));
            int constant0Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
            addInstruction(new IRStore(new IRPointerType(IRType.getUnsignedLongType()), new IRVirtualRegister(tempRegister), new IRConstant(constant0Index)));
        }

        retain(place, lcNewObject.theType);
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{typeName + ".<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{place}, null));

        IRType[] argumentTypes = new IRType[lcNewObject.arguments.size() + 1];
        IROperand[] args = new IROperand[lcNewObject.arguments.size() + 1];
        argumentTypes[0] = new IRPointerType(IRType.getVoidType());
        args[0] = place;
        retain(place, lcNewObject.theType);
        for (int i = 0; i < lcNewObject.arguments.size(); i++) {
            Type type = lcNewObject.arguments.get(i).theType;
            argumentTypes[i + 1] = parseType(type);
            this.visit(lcNewObject.arguments.get(i), additional);
            IROperand arg = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            retain(arg, type);
            args[i + 1] = arg;
        }
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{lcNewObject.constructorSymbol.getFullName()}), argumentTypes, args, null));

        operandStack.push(place);

        return null;
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object additional) {
        IRType elementType = parseType(lcNewArray.getRealType());
        int constantTypeSizeIndex = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(elementType)));
        IRConstant typeSize = new IRConstant(constantTypeSizeIndex);

        if (lcNewArray.place != null) {
            this.visit(lcNewArray.place, additional);
        } else {
            IROperand length;
            if (lcNewArray.elements != null) {
                int constantLengthIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), lcNewArray.elements.size()));
                length = new IRConstant(constantLengthIndex);
            } else {
                this.visit(lcNewArray.dimensions.getFirst(), additional);
                length = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
            }
            newArray(typeSize, length);
        }
        IROperand place = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        retain(place, lcNewArray.theType);

        if (lcNewArray.elements != null) {
            int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
            String temp = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(elementType), place, new IRConstant(constant16Index), new IRVirtualRegister(temp)));
            String address = allocateVirtualRegister();
            int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(address)));
            addInstruction(new IRStore(new IRPointerType(elementType), new IRVirtualRegister(address), new IRVirtualRegister(temp)));
            for (int i = 0; i < lcNewArray.elements.size(); i++) {
                LCExpression element = lcNewArray.elements.get(i);
                this.visit(element, additional);
                IROperand elem = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
                retain(elem, element.theType);
                String elementAddress = allocateVirtualRegister();
                addInstruction(new IRLoad(new IRPointerType(elementType), new IRVirtualRegister(address), new IRVirtualRegister(elementAddress)));
                addInstruction(new IRStore(elementType, new IRVirtualRegister(elementAddress), elem));
                String temp2 = allocateVirtualRegister();
                addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(elementType), new IRVirtualRegister(elementAddress), typeSize, new IRVirtualRegister(temp2)));
                addInstruction(new IRStore(new IRPointerType(elementType), new IRVirtualRegister(address), new IRVirtualRegister(temp2)));
            }
        } else {
            initArray(place, typeSize, lcNewArray.dimensions, 0);
        }
        operandStack.push(place);
        return null;
    }

    @Override
    public Object visitTernary(LCTernary lcTernary, Object additional) {
        IRType type = parseType(lcTernary.theType);

        this.visit(lcTernary.condition, additional);
        IROperand result = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        int constant1Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(type)));
        String tempRegister = allocateVirtualRegister();
        addInstruction(new IRStackAllocate(new IRConstant(constant1Index), new IRVirtualRegister(tempRegister)));

        IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getBooleanType(), IRCondition.IfFalse, result, null);
        addInstruction(irConditionalJump);

        createBasicBlock();
        this.visit(lcTernary.then, additional);
        IROperand thenResult = operandStack.isEmpty() ? null : operandStack.pop();


        addInstruction(new IRStore(type, new IRVirtualRegister(tempRegister), thenResult));
        IRGoto irGoto = new IRGoto(null);
        addInstruction(irGoto);

        IRBasicBlock elseBlock = createBasicBlock();

        this.visit(lcTernary._else, additional);
        IROperand elseResult = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        addInstruction(new IRStore(type, new IRVirtualRegister(tempRegister), elseResult));

        IRBasicBlock end = createBasicBlock();
        String resultRegister = allocateVirtualRegister();
        addInstruction(new IRLoad(type, new IRVirtualRegister(tempRegister), new IRVirtualRegister(resultRegister)));

        irConditionalJump.target = elseBlock.name;
        irGoto.target = end.name;

        operandStack.push(new IRVirtualRegister(resultRegister));
        return null;
    }

    @Override
    public Object visitDelete(LCDelete lcDelete, Object additional) {
        this.visit(lcDelete.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        deleteSomething(operand, lcDelete.expression.theType);
        return null;
    }

    @Override
    public Object visitClassof(LCClassof lcClassof, Object additional) {
        IRMacro macro = new IRMacro("global_data_address", new String[]{String.format("<class_instance %s>", lcClassof.typeExpression.theType.toTypeString())});
        operandStack.push(macro);
        return null;
    }

    @Override
    public Object visitInstanceof(LCInstanceof lcInstanceof, Object additional) {
        this.visit(lcInstanceof.expression, additional);
        IROperand operand = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        String operandClassInstanceAddressRegister = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getUnsignedLongType(), operand, new IRVirtualRegister(operandClassInstanceAddressRegister)));
        String classInstanceName = String.format("<class_instance %s>", lcInstanceof.typeExpression.theType.toTypeString());
        ClassSymbol classSymbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
        MethodSymbol methodSymbol = null;
        for (MethodSymbol method : classSymbol.methods) {
            if (method.name.equals("isSubClassOf")) {
                methodSymbol = method;
                break;
            }
        }
        String resultRegister = allocateVirtualRegister();
        IRMacro methodAddress = new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol).getFullName()});
        addInstruction(new IRInvoke(IRType.getBooleanType(), methodAddress, new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{new IRVirtualRegister(operandClassInstanceAddressRegister), new IRMacro("global_data_address", new String[]{classInstanceName})}, new IRVirtualRegister(resultRegister)));

        operandStack.push(new IRVirtualRegister(resultRegister));
        return null;
    }

    @Override
    public Object visitIn(LCIn lcIn, Object additional) {
        this.visit(lcIn.expression1, additional);
        IROperand operand1 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        this.visit(lcIn.expression2, additional);
        IROperand operand2 = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        callMethod(lcIn.symbol, List.of(operand2, operand1), List.of(lcIn.expression2.theType, lcIn.expression1.theType));
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object additional) {
        this.visitVariableDeclaration(lcForeach.init, additional);

        this.visit(lcForeach.source, additional);
        IROperand source = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        if (lcForeach.source.theType instanceof ArrayType arrayType) {
            IRType type = parseType(arrayType.base);

            arrayLength(source, arrayType.base);
            IROperand arrayLength = operandStack.pop();
            int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
            String count = allocateVirtualRegister();
            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(count)));
            int constant0Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(count), new IRConstant(constant0Index)));
            String elementAddress = allocateVirtualRegister();
            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(elementAddress)));
            int constant16Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
            String temp = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), source, new IRConstant(constant16Index), new IRVirtualRegister(temp)));
            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(elementAddress), new IRVirtualRegister(temp)));

            var condition = createBasicBlock();
            String temp2 = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(count), new IRVirtualRegister(temp2)));
            var conditionalJump = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.GreaterEqual, new IRVirtualRegister(temp2), arrayLength, "");
            addInstruction(conditionalJump);

            createBasicBlock();
            String temp3 = allocateVirtualRegister();
            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(elementAddress), new IRVirtualRegister(temp3)));
            String element = allocateVirtualRegister();
            addInstruction(new IRLoad(type, new IRVirtualRegister(temp3), new IRVirtualRegister(element)));
            retain(new IRVirtualRegister(element), arrayType.base);
            IRMacro address = new IRMacro("field_address", new String[]{this.variableName2FieldName.get(lcForeach.init.name).peek()});
            addInstruction(new IRStore(type, address, new IRVirtualRegister(element)));

            int constantElementSizeIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(parseType(arrayType.base))));
            String temp4 = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp3), new IRConstant(constantElementSizeIndex), new IRVirtualRegister(temp4)));
            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(elementAddress), new IRVirtualRegister(temp4)));
            String temp5 = allocateVirtualRegister();
            addInstruction(new IRIncrease(IRType.getUnsignedLongType(), new IRVirtualRegister(temp2), new IRVirtualRegister(temp5)));
            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(count), new IRVirtualRegister(temp5)));

            this.visit(lcForeach.body, additional);

            release(new IRVirtualRegister(element), arrayType.base);
            addInstruction(new IRGoto(condition.name));

            var end = createBasicBlock();
            conditionalJump.target = end.name;
        }

        this.variableName2FieldName.get(lcForeach.init.name).pop();

        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object additional) {
        super.visitWith(lcWith, additional);
        for (int i = lcWith.resources.size() - 1; i >= 0; i--) {
            var resource = lcWith.resources.get(i);
            var name = this.variableName2FieldName.get(resource.name).pop();
            callMethod(lcWith.methodSymbol, List.of(new IRMacro("field_address", new String[]{name})), List.of(resource.theType));
        }
        return null;
    }

    private void createClassInstance(LCObjectDeclaration lcObjectDeclaration) {
        int constantNullptrIndex = module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), 0));
        String vtableName = String.format("<vtable %s>", lcObjectDeclaration.getFullName());
        String itableName = String.format("<itable %s>", lcObjectDeclaration.getFullName());
        int itableLength = 0;
        boolean hasVTable;
        boolean hasITable;
        IROperand superClassInstanceAddress;
        switch (lcObjectDeclaration) {
            case LCClassDeclaration lcClassDeclaration -> {
                Map<String, String> virtualMethods = getVirtualMethods(lcClassDeclaration.symbol);
                IRVirtualTable vtable = new IRVirtualTable(virtualMethods.values().toArray(new String[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(vtableName, new IROperand[]{vtable}));
                this.module.name2VTableKeys.put(lcClassDeclaration.getFullName(), new ArrayList<>(virtualMethods.keySet()));

                Map<String, Map<String, String>> interfacesMethodMap = getInterfacesMethodMap(lcClassDeclaration.symbol);
                List<IRInterfaceTable.Entry> entries = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : interfacesMethodMap.entrySet())
                    entries.add(new IRInterfaceTable.Entry(entry.getKey(), entry.getValue().values().toArray(new String[0])));
                IRInterfaceTable itable = new IRInterfaceTable(entries.toArray(new IRInterfaceTable.Entry[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(itableName, new IROperand[]{itable}));
                itableLength = entries.size();

                superClassInstanceAddress = lcClassDeclaration.symbol.extended != null ? new IRMacro("global_data_address", new String[]{String.format("<class_instance %s>", lcClassDeclaration.symbol.extended.getFullName())}) : new IRConstant(constantNullptrIndex);
                hasVTable = true;
                hasITable = true;
            }
            case LCInterfaceDeclaration lcInterfaceDeclaration -> {
                // TODO
                superClassInstanceAddress = new IRConstant(constantNullptrIndex);
                hasVTable = false;
                hasITable = false;
            }
            case LCEnumDeclaration lcEnumDeclaration -> {
                Map<String, String> virtualMethods = getVirtualMethods(lcEnumDeclaration.symbol);
                IRVirtualTable vtable = new IRVirtualTable(virtualMethods.values().toArray(new String[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(vtableName, new IROperand[]{vtable}));
                this.module.name2VTableKeys.put(lcEnumDeclaration.getFullName(), new ArrayList<>(virtualMethods.keySet()));

                Map<String, Map<String, String>> interfacesMethodMap = getInterfacesMethodMap(lcEnumDeclaration.symbol);
                List<IRInterfaceTable.Entry> entries = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : interfacesMethodMap.entrySet())
                    entries.add(new IRInterfaceTable.Entry(entry.getKey(), entry.getValue().values().toArray(new String[0])));
                IRInterfaceTable itable = new IRInterfaceTable(entries.toArray(new IRInterfaceTable.Entry[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(itableName, new IROperand[]{itable}));
                itableLength = entries.size();

                superClassInstanceAddress = new IRMacro("global_data_address", new String[]{"<class_instance l.lang.Enum>"});
                hasVTable = true;
                hasITable = true;
            }
            case LCRecordDeclaration lcRecordDeclaration -> {
                Map<String, String> virtualMethods = getVirtualMethods(lcRecordDeclaration.symbol);
                IRVirtualTable vtable = new IRVirtualTable(virtualMethods.values().toArray(new String[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(vtableName, new IROperand[]{vtable}));
                this.module.name2VTableKeys.put(lcRecordDeclaration.getFullName(), new ArrayList<>(virtualMethods.keySet()));

                Map<String, Map<String, String>> interfacesMethodMap = getInterfacesMethodMap(lcRecordDeclaration.symbol);
                List<IRInterfaceTable.Entry> entries = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : interfacesMethodMap.entrySet())
                    entries.add(new IRInterfaceTable.Entry(entry.getKey(), entry.getValue().values().toArray(new String[0])));
                IRInterfaceTable itable = new IRInterfaceTable(entries.toArray(new IRInterfaceTable.Entry[0]));
                this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(itableName, new IROperand[]{itable}));
                itableLength = entries.size();

                superClassInstanceAddress = new IRMacro("global_data_address", new String[]{"<class_instance l.lang.Record>"});
                hasVTable = true;
                hasITable = true;
            }
            default -> throw new RuntimeException("Unsupported object declaration type");
        }
        String classInstanceName = String.format("<class_instance %s>", lcObjectDeclaration.getFullName());
        this.module.globalDataSection.add(new IRGlobalDataSection.GlobalData(classInstanceName, new IRMacro("structure_length", new String[]{SystemTypes.Class_Type.name})));
        IRMacro classInstanceAddress = new IRMacro("global_data_address", new String[]{classInstanceName});
        IRControlFlowGraph lastCFG = this.currentCFG;
        this.currentCFG = this.module.functions.get("<init>").controlFlowGraph;
        createBasicBlock();
        retain(classInstanceAddress, SystemTypes.Class_Type);
        retain(classInstanceAddress, SystemTypes.Class_Type);
        if (!(superClassInstanceAddress instanceof IRConstant constant) || constant.index != constantNullptrIndex) {
            retain(superClassInstanceAddress, SystemTypes.Class_Type);
        }
        ClassSymbol classSymbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
        String constructorName = classSymbol.constructors.getFirst().getFullName();
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{classSymbol.getFullName() + ".<__init__>()V"}), new IRType[]{new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstanceAddress}, null));
        int constantITableLengthIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), itableLength));
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRMacro("function_address", new String[]{constructorName}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(new IRPointerType(IRPointerType.getVoidType())), IRType.getUnsignedLongType(), new IRPointerType(new IRPointerType(new IRPointerType(IRType.getVoidType()))), new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstanceAddress, hasVTable ? new IRMacro("global_data_address", new String[]{vtableName}) : new IRConstant(constantNullptrIndex), new IRConstant(constantITableLengthIndex), hasITable ? new IRMacro("global_data_address", new String[]{itableName}) : new IRConstant(constantNullptrIndex), superClassInstanceAddress}, null));
        this.currentCFG = lastCFG;
    }

    private Map<String, String> getVirtualMethods(ClassSymbol classSymbol) {
        Map<String, String> result;
        if (classSymbol.extended == null) {
            result = new LinkedHashMap<>();
        } else {
            result = getVirtualMethods(classSymbol.extended);
        }
        for (MethodSymbol methodSymbol : classSymbol.methods) {
            if (LCFlags.hasStatic(methodSymbol.flags)) continue;
            result.put(methodSymbol.getSimpleName(), LCFlags.hasAbstract(methodSymbol.flags) ? "" : methodSymbol.getFullName());
        }
        if (classSymbol.destructor != null) {
            result.put(classSymbol.destructor.getSimpleName(), classSymbol.destructor.getFullName());
        }
        return result;
    }

    private Map<String, String> getVirtualMethods(RecordSymbol recordSymbol) {
        Map<String, String> result = getVirtualMethods(((LCClassDeclaration) Objects.requireNonNull(getAST(recordSymbol.declaration).getObjectDeclaration(SystemTypes.Record_Type.name))).symbol);
        for (MethodSymbol methodSymbol : recordSymbol.methods) {
            if (LCFlags.hasStatic(methodSymbol.flags)) continue;
            result.put(methodSymbol.getSimpleName(), LCFlags.hasAbstract(methodSymbol.flags) ? "" : methodSymbol.getFullName());
        }
        if (recordSymbol.destructor != null) {
            result.put(recordSymbol.destructor.getSimpleName(), recordSymbol.destructor.getFullName());
        }
        return result;
    }

    private Map<String, String> getVirtualMethods(EnumSymbol enumSymbol) {
        Map<String, String> result = getVirtualMethods(((LCClassDeclaration) Objects.requireNonNull(getAST(enumSymbol.declaration).getObjectDeclaration(SystemTypes.Enum_Type.name))).symbol);
        for (MethodSymbol methodSymbol : enumSymbol.methods) {
            if (LCFlags.hasStatic(methodSymbol.flags)) continue;
            result.put(methodSymbol.getSimpleName(), LCFlags.hasAbstract(methodSymbol.flags) ? "" : methodSymbol.getFullName());
        }
        if (enumSymbol.destructor != null) {
            result.put(enumSymbol.destructor.getSimpleName(), enumSymbol.destructor.getFullName());
        }
        return result;
    }

    private Map<String, Map<String, String>> getInterfacesMethodMap(ClassSymbol classSymbol) {
        Map<String, Map<String, String>> result;
        if (classSymbol.extended != null) {
            result = getInterfacesMethodMap(classSymbol.extended);
        } else {
            result = new LinkedHashMap<>();
        }
        Queue<InterfaceSymbol> queue = new LinkedList<>(classSymbol.implementedInterfaces);
        while (!queue.isEmpty()) {
            InterfaceSymbol interfaceSymbol = queue.poll();
            Map<String, String> map = new LinkedHashMap<>();
            for (MethodSymbol methodSymbol : interfaceSymbol.methods) {
                MethodSymbol symbol = classSymbol.getMethodCascade(methodSymbol.getSimpleName());
                map.put(methodSymbol.getSimpleName(), symbol != null ? symbol.getFullName() : "");
            }
            map.put("<deinit>()V", classSymbol.getFullName() + ".<deinit>()V");
            result.put(interfaceSymbol.getFullName(), map);
            queue.addAll(interfaceSymbol.extendedInterfaces);
        }
        return result;
    }

    private Map<String, Map<String, String>> getInterfacesMethodMap(RecordSymbol recordSymbol) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        Queue<InterfaceSymbol> queue = new LinkedList<>(recordSymbol.implementedInterfaces);
        while (!queue.isEmpty()) {
            InterfaceSymbol interfaceSymbol = queue.poll();
            Map<String, String> map = new LinkedHashMap<>();
            for (MethodSymbol methodSymbol : interfaceSymbol.methods) {
                MethodSymbol symbol = recordSymbol.getMethodCascade(methodSymbol.getSimpleName());
                map.put(methodSymbol.getSimpleName(), symbol != null ? symbol.getFullName() : "");
            }
            map.put("<deinit>()V", recordSymbol.getFullName() + ".<deinit>()V");
            result.put(interfaceSymbol.getFullName(), map);
            queue.addAll(interfaceSymbol.extendedInterfaces);
        }
        return result;
    }

    private Map<String, Map<String, String>> getInterfacesMethodMap(EnumSymbol enumSymbol) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        Queue<InterfaceSymbol> queue = new LinkedList<>(enumSymbol.implementedInterfaces);
        while (!queue.isEmpty()) {
            InterfaceSymbol interfaceSymbol = queue.poll();
            Map<String, String> map = new LinkedHashMap<>();
            for (MethodSymbol methodSymbol : interfaceSymbol.methods) {
                MethodSymbol symbol = enumSymbol.getMethodCascade(methodSymbol.getSimpleName());
                map.put(methodSymbol.getSimpleName(), symbol != null ? symbol.getFullName() : "");
            }
            map.put("<deinit>()V", enumSymbol.getFullName() + ".<deinit>()V");
            result.put(interfaceSymbol.getFullName(), map);
            queue.addAll(interfaceSymbol.extendedInterfaces);
        }
        return result;
    }

    private void initObjectHead(String objectName) {
        this.getThisInstance();
        IROperand thisInstance = operandStack.pop();
        addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), thisInstance, new IRMacro("global_data_address", new String[]{"<class_instance " + objectName + ">"})));
    }

    private void callMethod(MethodSymbol methodSymbol, List<IROperand> arguments, List<Type> types) {
        callMethod(methodSymbol, arguments, types, false);
    }

    private void callMethod(MethodSymbol methodSymbol, List<IROperand> arguments, List<Type> types, boolean forceDirectlyInvocation) {
        IROperand address;
        if (forceDirectlyInvocation || LCFlags.hasStatic(methodSymbol.flags) || LCFlags.hasFinal(methodSymbol.flags) || methodSymbol.methodKind == MethodKind.Constructor) {
            address = new IRMacro("function_address", new String[]{methodSymbol.getFullName()});
        } else {
            switch (methodSymbol.objectSymbol) {
                case ClassSymbol classSymbol -> {
                    String classInstanceAddressRegister = allocateVirtualRegister();
                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), arguments.getFirst(), new IRVirtualRegister(classInstanceAddressRegister)));
                    String temp1 = allocateVirtualRegister();
                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRMacro("field_address", new String[]{SystemTypes.Class_Type.name, "vtable"}, new IROperand[]{new IRVirtualRegister(classInstanceAddressRegister)}), new IRVirtualRegister(temp1)));
                    String temp2 = allocateVirtualRegister();
                    addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp1), new IRMacro("vtable_entry_offset", new String[]{classSymbol.getFullName(), methodSymbol.getSimpleName()}), new IRVirtualRegister(temp2)));
                    String result = allocateVirtualRegister();
                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp2), new IRVirtualRegister(result)));
                    address = new IRVirtualRegister(result);
                }
                case InterfaceSymbol interfaceSymbol -> {
                    String classInstanceAddressRegister = allocateVirtualRegister();
                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), arguments.getFirst(), new IRVirtualRegister(classInstanceAddressRegister)));
                    ClassSymbol symbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
                    MethodSymbol methodSymbol2 = null;
                    for (MethodSymbol method : symbol.methods) {
                        if (method.name.equals("getITableEntry")) {
                            methodSymbol2 = method;
                            break;
                        }
                    }
                    IRMacro interfaceClassInstance = new IRMacro("global_data_address", new String[]{"<class_instance " + interfaceSymbol.getFullName() + ">"});
                    IRVirtualRegister classInstance = new IRVirtualRegister(classInstanceAddressRegister);
                    retain(classInstance, SystemTypes.Class_Type);
                    retain(interfaceClassInstance, SystemTypes.Class_Type);
                    String itableAddressRegister = allocateVirtualRegister();
                    addInstruction(new IRInvoke(new IRPointerType(IRType.getVoidType()), new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol2).getFullName()}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstance, interfaceClassInstance}, new IRVirtualRegister(itableAddressRegister)));
                    String temp = allocateVirtualRegister();
                    addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(itableAddressRegister), new IRMacro("itable_entry_offset", new String[]{interfaceSymbol.getFullName(), methodSymbol.getSimpleName()}), new IRVirtualRegister(temp)));
                    String addressRegister = allocateVirtualRegister();
                    addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp), new IRVirtualRegister(addressRegister)));
                    address = new IRVirtualRegister(addressRegister);
                }
                case EnumSymbol enumSymbol -> {
                    address = null;
                }
                case RecordSymbol recordSymbol -> {
                    address = null;
                }
                case AnnotationSymbol annotationSymbol -> {
                    address = null;
                }
            }
        }
        List<IRType> irTypes = new ArrayList<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            Type type = types.get(i);
            retain(arguments.get(i), type);
            irTypes.add(parseType(type));
        }
        String resultRegister;
        if (methodSymbol.returnType.equals(SystemTypes.VOID)) {
            resultRegister = null;
        } else {
            resultRegister = allocateVirtualRegister();
        }
        addInstruction(new IRInvoke(parseType(methodSymbol.returnType), address, irTypes.toArray(new IRType[0]), arguments.toArray(new IROperand[0]), resultRegister != null ? new IRVirtualRegister(resultRegister) : null));

        if (resultRegister != null) {
            operandStack.push(new IRVirtualRegister(resultRegister));
        }
    }

    private void newArray(IROperand typeSize, IROperand length) {
        String tempRegister = allocateVirtualRegister();
        addInstruction(new IRCalculate(false, IRCalculate.Operator.MUL, IRType.getUnsignedLongType(), typeSize, length, new IRVirtualRegister(tempRegister)));
        int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
        String sizeRegister = allocateVirtualRegister();
        addInstruction(new IRCalculate(false, IRCalculate.Operator.ADD, IRType.getUnsignedLongType(), new IRVirtualRegister(tempRegister), new IRConstant(constant16Index), new IRVirtualRegister(sizeRegister)));
        String placeRegister = allocateVirtualRegister();
        addInstruction(new IRMalloc(new IRVirtualRegister(sizeRegister), new IRVirtualRegister(placeRegister)));

        operandStack.push(new IRVirtualRegister(placeRegister));
    }

    private void initArrayHead(IROperand place) {

    }

    private void initArray(IROperand place, IROperand typeSize, List<LCExpression> dimensions, int index) {
        if (index + 1 >= dimensions.size()) return;

        LCExpression dimension = dimensions.get(index);
        LCExpression length = dimensions.get(index + 1);
        if (dimension == null || length == null) return;

        this.visit(dimension, null);
        IROperand dim = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        this.visit(length, null);
        IROperand len = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();

        String address = allocateVirtualRegister();
        int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
        addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(address)));
        int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
        String temp = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), place, new IRConstant(constant16Index), new IRVirtualRegister(temp)));
        addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(address), new IRVirtualRegister(temp)));
        String countRegister = allocateVirtualRegister();
        addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(countRegister)));
        int constant0Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
        addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRConstant(constant0Index)));

        IRBasicBlock condition = createBasicBlock();
        String temp2 = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp2)));
        IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.GreaterEqual, new IRVirtualRegister(temp2), dim, "");
        addInstruction(irConditionalJump);

        createBasicBlock();
        newArray(typeSize, len);
        IROperand newPlace = operandStack.isEmpty() ? new IRConstant(-1) : operandStack.pop();
        initArrayHead(newPlace);
        initArray(newPlace, typeSize, dimensions, index + 1);
        String temp3 = allocateVirtualRegister();
        addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(address), new IRVirtualRegister(temp3)));
        addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(temp3), newPlace));
        String temp4 = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp3), typeSize, new IRVirtualRegister(temp4)));
        addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(address), new IRVirtualRegister(temp4)));

        String temp5 = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp5)));
        String temp6 = allocateVirtualRegister();
        addInstruction(new IRIncrease(IRType.getUnsignedLongType(), new IRVirtualRegister(temp5), new IRVirtualRegister(temp6)));
        addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp6)));
        addInstruction(new IRGoto(condition.name));

        IRBasicBlock end = createBasicBlock();

        irConditionalJump.target = end.name;
    }

    private void arrayLength(IROperand array, Type elementType) {
        int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
        String tempRegister1 = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.SUB, new IRPointerType(IRType.getUnsignedLongType()), array, new IRConstant(constant8Index), new IRVirtualRegister(tempRegister1)));
        String tempRegister2 = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(tempRegister1), new IRVirtualRegister(tempRegister2)));
        String temp3 = allocateVirtualRegister();
        int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
        addInstruction(new IRCalculate(IRCalculate.Operator.SUB, IRType.getUnsignedLongType(), new IRVirtualRegister(tempRegister2), new IRConstant(constant16Index), new IRVirtualRegister(temp3)));
        int constantElementSizeIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(parseType(elementType))));
        String lengthRegister = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.DIV, IRType.getUnsignedLongType(), new IRVirtualRegister(temp3), new IRConstant(constantElementSizeIndex), new IRVirtualRegister(lengthRegister)));
        operandStack.push(new IRVirtualRegister(lengthRegister));
    }

    private void deleteSomething(IROperand operand, Type type) {
        if (type instanceof ArrayType arrayType) deleteArray(operand, arrayType);
        else if (type instanceof NamedType namedType && !SystemTypes.isPrimitiveType(namedType))
            deleteObject(operand, namedType);
    }

    private void deleteObject(IROperand object, NamedType objectType) {
        String classInstanceAddressRegister = allocateVirtualRegister();
        addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), object, new IRVirtualRegister(classInstanceAddressRegister)));
        ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(ast.getObjectDeclaration(objectType.name)));
        String destructorAddress = allocateVirtualRegister();
        if (objectSymbol instanceof InterfaceSymbol interfaceSymbol) {
            ClassSymbol classSymbol = ((LCClassDeclaration) Objects.requireNonNull(this.ast.getObjectDeclaration(SystemTypes.Class_Type.name))).symbol;
            MethodSymbol methodSymbol = null;
            for (MethodSymbol method : classSymbol.methods) {
                if (method.name.equals("getITableEntry")) {
                    methodSymbol = method;
                    break;
                }
            }
            IRMacro interfaceClassInstance = new IRMacro("global_data_address", new String[]{"<class_instance " + interfaceSymbol.getFullName() + ">"});
            IRVirtualRegister classInstance = new IRVirtualRegister(classInstanceAddressRegister);
            retain(classInstance, SystemTypes.Class_Type);
            retain(interfaceClassInstance, SystemTypes.Class_Type);
            String itableAddressRegister = allocateVirtualRegister();
            addInstruction(new IRInvoke(new IRPointerType(IRType.getVoidType()), new IRMacro("function_address", new String[]{Objects.requireNonNull(methodSymbol).getFullName()}), new IRType[]{new IRPointerType(IRType.getVoidType()), new IRPointerType(IRType.getVoidType())}, new IROperand[]{classInstance, interfaceClassInstance}, new IRVirtualRegister(itableAddressRegister)));
            String temp = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(itableAddressRegister), new IRMacro("itable_entry_offset", new String[]{interfaceSymbol.getFullName(), "<deinit>()V"}), new IRVirtualRegister(temp)));
            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp), new IRVirtualRegister(destructorAddress)));
        } else {
            String vtableAddressRegister = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(classInstanceAddressRegister), new IRMacro("structure_field_offset", new String[]{SystemTypes.Class_Type.name, "vtable"}), new IRVirtualRegister(vtableAddressRegister)));
            String temp1 = allocateVirtualRegister();
            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(vtableAddressRegister), new IRVirtualRegister(temp1)));
            String temp2 = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp1), new IRMacro("vtable_entry_offset", new String[]{objectType.name, "<deinit>()V"}), new IRVirtualRegister(temp2)));
            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp2), new IRVirtualRegister(destructorAddress)));
        }
        addInstruction(new IRInvoke(IRType.getVoidType(), new IRVirtualRegister(destructorAddress), new IRType[]{parseType(objectType)}, new IROperand[]{object}, null));
        addInstruction(new IRFree(object));
    }

    private void deleteArray(IROperand array, ArrayType arrayType) {
        if (!SystemTypes.isPrimitiveType(arrayType.base)) {
            int constantTypeSizeIndex = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), IRType.getLength(parseType(arrayType.base))));

            String addressRegister = allocateVirtualRegister();
            int constant8Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(addressRegister)));
            String temp = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.SUB, parseType(arrayType), array, new IRConstant(constant8Index), new IRVirtualRegister(temp)));
            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp)));
            String sizeRegister0 = allocateVirtualRegister();
            addInstruction(new IRLoad(new IRPointerType(IRType.getUnsignedLongType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(sizeRegister0)));
            String sizeRegister1 = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister0), new IRVirtualRegister(sizeRegister1)));
            String sizeRegister = allocateVirtualRegister();
            int constant16Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 16));
            addInstruction(new IRCalculate(IRCalculate.Operator.SUB, IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister1), new IRConstant(constant16Index), new IRVirtualRegister(sizeRegister)));
            String lengthRegister = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.DIV, IRType.getUnsignedLongType(), new IRVirtualRegister(sizeRegister), new IRConstant(constantTypeSizeIndex), new IRVirtualRegister(lengthRegister)));
            String temp2 = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, parseType(arrayType), array, new IRConstant(constant16Index), new IRVirtualRegister(temp2)));
            addInstruction(new IRStore(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp2)));

            String countRegister = allocateVirtualRegister();
            addInstruction(new IRStackAllocate(new IRConstant(constant8Index), new IRVirtualRegister(countRegister)));
            int constant0Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRConstant(constant0Index)));

            IRBasicBlock conditionBlock = createBasicBlock();
            String temp3 = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp3)));
            IRConditionalJump irConditionalJump = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.GreaterEqual, new IRVirtualRegister(temp3), new IRVirtualRegister(lengthRegister), null);
            addInstruction(irConditionalJump);
            createBasicBlock();

            String temp4 = allocateVirtualRegister();
            addInstruction(new IRLoad(new IRPointerType(new IRPointerType(IRType.getVoidType())), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp4)));
            String elementRegister = allocateVirtualRegister();
            addInstruction(new IRLoad(new IRPointerType(IRType.getVoidType()), new IRVirtualRegister(temp4), new IRVirtualRegister(elementRegister)));
            release(new IRVirtualRegister(elementRegister), arrayType.base);

            String temp5 = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp5)));
            String temp6 = allocateVirtualRegister();
            addInstruction(new IRCalculate(IRCalculate.Operator.ADD, IRType.getUnsignedLongType(), new IRVirtualRegister(temp5), new IRConstant(constantTypeSizeIndex), new IRVirtualRegister(temp6)));
            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(addressRegister), new IRVirtualRegister(temp6)));
            String temp7 = allocateVirtualRegister();
            addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp7)));
            String temp8 = allocateVirtualRegister();
            addInstruction(new IRIncrease(IRType.getUnsignedLongType(), new IRVirtualRegister(temp7), new IRVirtualRegister(temp8)));
            addInstruction(new IRStore(IRType.getUnsignedLongType(), new IRVirtualRegister(countRegister), new IRVirtualRegister(temp8)));
            addInstruction(new IRGoto(conditionBlock.name));
            IRBasicBlock end = createBasicBlock();
            irConditionalJump.target = end.name;
        }

        addInstruction(new IRFree(array));
    }

    private void retain(IROperand operand, Type type) {
        if (!SystemTypes.isReference(type)) return;

        IRConditionalJump irConditionalJump;
        if (type instanceof NullableType) {
            int constantNullIndex = module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), null));
            irConditionalJump = new IRConditionalJump(new IRPointerType(IRType.getVoidType()), IRCondition.Equal, operand, new IRConstant(constantNullIndex), null);
            addInstruction(irConditionalJump);
            createBasicBlock();
        } else {
            irConditionalJump = null;
        }

        int constant8Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
        String tempRegister = allocateVirtualRegister();
        addInstruction(new IRCalculate(false, IRCalculate.Operator.ADD, new IRPointerType(IRType.getUnsignedLongType()), operand, new IRConstant(constant8Index), new IRVirtualRegister(tempRegister)));
        addInstruction(new IRIncrease(IRType.getUnsignedLongType(), new IRVirtualRegister(tempRegister)));

        if (irConditionalJump != null) {
            var end = createBasicBlock();
            irConditionalJump.target = end.name;
        }
    }

    private void release(IROperand operand, Type type) {
        if (!SystemTypes.isReference(type)) return;

        IRConditionalJump irConditionalJump;
        if (type instanceof NullableType) {
            int constantNullIndex = module.constantPool.put(new IRConstantPool.Entry(new IRPointerType(IRType.getVoidType()), null));
            irConditionalJump = new IRConditionalJump(new IRPointerType(IRType.getVoidType()), IRCondition.Equal, operand, new IRConstant(constantNullIndex), null);
            addInstruction(irConditionalJump);
            createBasicBlock();
        } else {
            irConditionalJump = null;
        }

        int constant8Index = this.module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 8));
        String temp = allocateVirtualRegister();
        addInstruction(new IRCalculate(IRCalculate.Operator.ADD, new IRPointerType(IRType.getUnsignedLongType()), operand, new IRConstant(constant8Index), new IRVirtualRegister(temp)));
        addInstruction(new IRDecrease(IRType.getUnsignedLongType(), new IRVirtualRegister(temp)));

        String temp2 = allocateVirtualRegister();
        addInstruction(new IRLoad(IRType.getUnsignedLongType(), new IRVirtualRegister(temp), new IRVirtualRegister(temp2)));
        int constant0Index = module.constantPool.put(new IRConstantPool.Entry(IRType.getUnsignedLongType(), 0));
        IRConditionalJump irConditionalJump2 = new IRConditionalJump(IRType.getUnsignedLongType(), IRCondition.NotEqual, new IRVirtualRegister(temp2), new IRConstant(constant0Index), "");
        addInstruction(irConditionalJump2);
        createBasicBlock();
        deleteSomething(operand, type instanceof NullableType nullableType ? nullableType.base : type);

        var end = createBasicBlock();
        irConditionalJump2.target = end.name;
        if (irConditionalJump != null) {
            irConditionalJump.target = end.name;
        }
    }

    private void releaseScope(Scope scope) {
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                if (!SystemTypes.isReference(variableSymbol.theType)) continue;
                boolean ret = getVariable(variableSymbol, false);
                if (!ret) continue;
                IROperand operand = operandStack.pop();
                release(operand, variableSymbol.theType);
            }
        }
    }

    private static IRType parseType(Type type) {
        if (type.equals(SystemTypes.BYTE)) {
            return IRType.getByteType();
        } else if (type.equals(SystemTypes.SHORT)) {
            return IRType.getByteType();
        } else if (type.equals(SystemTypes.INT)) {
            return IRType.getIntType();
        } else if (type.equals(SystemTypes.LONG)) {
            return IRType.getLongType();
        } else if (type.equals(SystemTypes.UNSIGNED_BYTE)) {
            return IRType.getUnsignedByteType();
        } else if (type.equals(SystemTypes.UNSIGNED_SHORT)) {
            return IRType.getUnsignedShortType();
        } else if (type.equals(SystemTypes.UNSIGNED_INT)) {
            return IRType.getUnsignedIntType();
        } else if (type.equals(SystemTypes.UNSIGNED_LONG)) {
            return IRType.getUnsignedLongType();
        } else if (type.equals(SystemTypes.FLOAT)) {
            return IRType.getFloatType();
        } else if (type.equals(SystemTypes.DOUBLE)) {
            return IRType.getDoubleType();
        } else if (type.equals(SystemTypes.BOOLEAN)) {
            return IRType.getBooleanType();
        } else if (type.equals(SystemTypes.CHAR)) {
            return IRType.getCharType();
        } else if (type.equals(SystemTypes.VOID)) {
            return IRType.getVoidType();
        } else if (type instanceof PointerType pointerType) {
            return new IRPointerType(parseType(pointerType.base));
        } else if (type instanceof ReferenceType referenceType) {
            return new IRPointerType(parseType(referenceType.base));
        } else if (type instanceof NullableType nullableType) {
            return parseType(nullableType.base);
        } else if (type instanceof ArrayType arrayType) {
            return new IRPointerType(parseType(arrayType.base));
        } else {
            return new IRPointerType(IRType.getVoidType());
        }
    }

    private IRTypeCast.Kind parseTypeCast(Type originalType, Type targetType) {
        if (targetType instanceof PointerType) {
            return IRTypeCast.Kind.ZeroExtend;
        } else if (SystemTypes.isUnsignedIntegerType(targetType)) {
            if (isNeedsTruncate(originalType, targetType)) return IRTypeCast.Kind.Truncate;
            else if (SystemTypes.isUnsignedIntegerType(originalType)) return IRTypeCast.Kind.ZeroExtend;
            else if (SystemTypes.isDecimalType(originalType)) return IRTypeCast.Kind.FloatToInt;
            else return IRTypeCast.Kind.SignExtend;
        } else if (SystemTypes.isSignedIntegerType(targetType)) {
            if (isNeedsTruncate(originalType, targetType)) return IRTypeCast.Kind.Truncate;
            else if (SystemTypes.isDecimalType(originalType)) return IRTypeCast.Kind.FloatToInt;
            else return IRTypeCast.Kind.ZeroExtend;
        } else if (SystemTypes.isDecimalType(targetType)) {
            if (isNeedsTruncate(originalType, targetType)) return IRTypeCast.Kind.FloatTruncate;
            else if (SystemTypes.isDecimalType(originalType)) return IRTypeCast.Kind.FloatExtend;
            else return IRTypeCast.Kind.IntToFloat;
        } else {
            return IRTypeCast.Kind.Truncate;
        }
    }

    private IRCalculate.Operator parseArithmeticOperator(Tokens.Operator _operator) {
        return switch (_operator) {
            case Plus -> IRCalculate.Operator.ADD;
            case Minus -> IRCalculate.Operator.SUB;
            case Multiply -> IRCalculate.Operator.MUL;
            case Divide -> IRCalculate.Operator.DIV;
            case Modulus -> IRCalculate.Operator.MOD;
            case BitAnd -> IRCalculate.Operator.AND;
            case BitOr -> IRCalculate.Operator.OR;
            case BitXor -> IRCalculate.Operator.XOR;
            case LeftShiftArithmetic -> IRCalculate.Operator.SHL;
            case RightShiftArithmetic -> IRCalculate.Operator.SHR;
            case RightShiftLogical -> IRCalculate.Operator.USHR;
            default -> throw new RuntimeException("Invalid arithmetic operator");
        };
    }

    private static boolean isNeedsTruncate(Type originalType, Type targetType) {
        if ((SystemTypes.isIntegerType(originalType) && SystemTypes.isIntegerType(targetType)) || (SystemTypes.isDecimalType(originalType) && SystemTypes.isDecimalType(targetType))) {
            return getTypeNumber(originalType) > getTypeNumber(targetType);
        } else {
            return false;
        }
    }

    private static int getTypeNumber(Type type) {
        if (SystemTypes.BOOLEAN.equals(type)) return 1;
        else if (SystemTypes.BYTE.equals(type) || SystemTypes.UNSIGNED_BYTE.equals(type)) return 2;
        else if (SystemTypes.SHORT.equals(type) || SystemTypes.UNSIGNED_SHORT.equals(type)) return 3;
        else if (SystemTypes.INT.equals(type) || SystemTypes.UNSIGNED_INT.equals(type) || SystemTypes.CHAR.equals(type))
            return 4;
        else if (SystemTypes.LONG.equals(type) || SystemTypes.UNSIGNED_LONG.equals(type)) return 5;
        else if (SystemTypes.FLOAT.equals(type)) return 6;
        else if (SystemTypes.DOUBLE.equals(type)) return 7;
        else return 0;
    }
    */
}
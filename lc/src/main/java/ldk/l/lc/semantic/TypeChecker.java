package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.expression.literal.LCIntegerLiteral;
import ldk.l.lc.ast.expression.literal.LCNullLiteral;
import ldk.l.lc.ast.expression.type.LCPointerTypeExpression;
import ldk.l.lc.ast.expression.type.LCPredefinedTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.util.ConstValue;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionStatement;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.expression.LCMethodCall;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.expression.LCIf;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.LCThrow;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.*;
import ldk.l.lc.token.CharStream;
import ldk.l.lc.token.Token;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.*;
import ldk.l.lc.util.symbol.object.ObjectSymbol;
import ldk.l.util.option.Options;

import java.util.*;

public final class TypeChecker extends LCAstVisitor {
    private final SemanticAnalyzer semanticAnalyzer;
    private final ErrorStream errorStream;
    private final Options options;
    private final Stack<HashMap<LCExpression, Type>> expressionTypeRanges = new Stack<>();
    private final Stack<HashMap<LCExpression, ConstValue>> expressionValueRanges = new Stack<>();
    private boolean inIfCondition = false;

    public TypeChecker(SemanticAnalyzer semanticAnalyzer, ErrorStream errorStream, Options options) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.errorStream = errorStream;
        this.options = options;
    }

    private void pushNewMaps() {
        this.expressionTypeRanges.push(this.expressionTypeRanges.isEmpty() ? new HashMap<>() : this.cloneMap(this.expressionTypeRanges.peek()));
        this.expressionValueRanges.push(this.expressionValueRanges.isEmpty() ? new HashMap<>() : this.cloneMap(this.expressionValueRanges.peek()));
    }

    private void popMaps() {
        this.expressionTypeRanges.pop();
        this.expressionValueRanges.pop();
    }

    // TODO 处理类型域(varTypeRanges)
    private <K, V> HashMap<K, V> cloneMap(HashMap<K, V> map1) {
        HashMap<K, V> map2 = new HashMap<>();
        if (map1 == null) return map2;
        map2.putAll(map1);
        return map2;
    }

    private HashMap<VariableSymbol, Type> getComplementRanges(HashMap<VariableSymbol, Type> map) {
        HashMap<VariableSymbol, Type> map2 = new HashMap<>();
        for (VariableSymbol symbol : map.keySet()) {
            Type t = map.get(symbol);
            map2.put(symbol, TypeUtil.getComplementType(t));
        }
        return map2;
    }

    private HashMap<VariableSymbol, Type> intersectRanges(HashMap<VariableSymbol, Type> map1, HashMap<VariableSymbol, Type> map2) {
        HashMap<VariableSymbol, Type> map3 = new HashMap<>();
        for (VariableSymbol symbol : map1.keySet()) {
            Type t1 = map1.get(symbol);
            if (map2.containsKey(symbol)) {
                Type t2 = map2.get(symbol);
                Type t3 = TypeUtil.LE(t1, t2) ? t1 : (TypeUtil.LE(t2, t1) ? t2 : SystemTypes.AUTO);
                map3.put(symbol, t3);
            } else {
                map3.put(symbol, t1);
            }
        }
        for (VariableSymbol symbol : map2.keySet()) {
            Type t2 = map2.get(symbol);
            if (!map1.containsKey(symbol))
                map3.put(symbol, t2);
        }
        return map3;
    }

    protected void setVarConstValue(VariableSymbol variableSymbol, Object v) {
//        Type t = TypeUtil.createTypeByValue(v);
//        this.varRanges.put(varSymbol, t);
    }

    private Object getVarConstValue(VariableSymbol variableSymbol) {
        return null;
    }

    private LCVariable makeVariableBySymbol(VariableSymbol variableSymbol) {
        LCVariable variable = new LCVariable(variableSymbol.name, variableSymbol.declaration.position, false);
        variable.symbol = variableSymbol;
        return variable;
    }

    protected void setExpressionType(LCExpression expression, Type type) {
        this.expressionTypeRanges.peek().put(expression, type);
    }

    protected Type getDynamicType(LCExpression expression) {
        if (!this.expressionTypeRanges.isEmpty()) {
            HashMap<LCExpression, Type> expressionTypeRange = this.expressionTypeRanges.peek();
            if (expressionTypeRange.containsKey(expression)) return expressionTypeRange.get(expression);
        }
        return expression.theType;
    }

    private void dumpRange() {
//        for (Map.Entry<LCExpression, Type> entry : this.expressionTypeRanges.peek().entrySet()) {
//            System.out.println(entry.getKey().position);
//            System.out.println("'" + this.charStream.getStringByPosition(entry.getKey().position) + "' -> " + entry.getValue());
//        }
    }

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for main program:");
        }

        super.visitAst(ast, null);

        if (ast.mainMethod == null && ast.mainObjectDeclaration != null)
            ast.mainMethod = LCAstUtil.getMainMethod(ast.mainObjectDeclaration);

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for main program.");
            System.out.println("The end.");
        }

        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for source file '" + lcSourceCodeFile.filepath + "':");
        }

        this.pushNewMaps();
        super.visitSourceCodeFile(lcSourceCodeFile, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for source file '" + lcSourceCodeFile.filepath + "'.");
        }

        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for class '" + lcClassDeclaration.getFullName() + "':");
        }

        this.pushNewMaps();
        super.visitClassDeclaration(lcClassDeclaration, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for class '" + lcClassDeclaration.getFullName() + "'.");
        }

        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for interface '" + lcInterfaceDeclaration.getFullName() + "':");
        }

        this.pushNewMaps();
        super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for interface '" + lcInterfaceDeclaration.getFullName() + "'.");
        }

        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for annotation '" + lcAnnotationDeclaration.getFullName() + "':");
        }

        this.pushNewMaps();
        super.visitAnnotationDeclaration(lcAnnotationDeclaration, additional);
        this.popMaps();

        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for enum '" + lcEnumDeclaration.getFullName() + "':");
        }

        this.pushNewMaps();
        super.visitEnumDeclaration(lcEnumDeclaration, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for enum '" + lcEnumDeclaration.getFullName() + "'.");
        }

        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for record '" + lcRecordDeclaration.getFullName() + "':");
        }

        this.pushNewMaps();
        super.visitRecordDeclaration(lcRecordDeclaration, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for record '" + lcRecordDeclaration.getFullName() + "'.");
        }

        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Enter type checker for method '" + lcMethodDeclaration.name + "':");
        }

        this.pushNewMaps();
        super.visitMethodDeclaration(lcMethodDeclaration, additional);
        this.popMaps();

        if (this.options.get("traceTypeChecker", Boolean.class)) {
            System.out.println("Exit type checker for method '" + lcMethodDeclaration.name + "'.");
        }

        return null;
    }

    @Override
    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object additional) {
        Object result = this.visit(lcExpressionStatement.expression, additional);
        if (result instanceof LCExpression expression) lcExpressionStatement.expression = expression;

        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        super.visitVariableDeclaration(lcVariableDeclaration, additional);

        if (lcVariableDeclaration.init != null) {
            Type t2 = this.getDynamicType(lcVariableDeclaration.init);
            if ((lcVariableDeclaration.theType == null || lcVariableDeclaration.theType.equals(SystemTypes.AUTO)) && t2 != null) {
                lcVariableDeclaration.theType = t2;
                lcVariableDeclaration.symbol.theType = t2;
            }
            Type t1 = lcVariableDeclaration.theType;
            if (t1 != null && t2 != null) {
                if (SystemTypes.isWrapperType(t1) && SystemTypes.isPrimitiveType(t2) && t1.equals(SystemTypes.getWrapperTypeByPrimitiveType(t2))) {
                    LCExpression init = lcVariableDeclaration.init;

                    NamedType namedType = (NamedType) t1;
                    LCTypeReferenceExpression typeReferenceExpression = new LCTypeReferenceExpression(namedType.name, Position.origin);
                    typeReferenceExpression.theType = namedType;
                    LCBinary binary = new LCBinary(Tokens.Operator.Dot, typeReferenceExpression, new LCMethodCall("valueOf", init.position, new ArrayList<>(), new ArrayList<>(List.of(init)), init.position, init.isErrorNode), init.isErrorNode);
                    binary.parentNode = lcVariableDeclaration;
                    this.semanticAnalyzer.referenceResolver.visitBinary(binary, additional);
                    this.visitBinary(binary, additional);
                    lcVariableDeclaration.init = binary;
                } else if (SystemTypes.isPrimitiveType(t1) && SystemTypes.isWrapperType(t2) && t2.equals(SystemTypes.getWrapperTypeByPrimitiveType(t1))) {
                    LCExpression init = lcVariableDeclaration.init;
                    LCBinary binary = new LCBinary(Tokens.Operator.Dot, init, new LCMethodCall("getValue", Position.origin, new ArrayList<>(), new ArrayList<>(), init.position, init.isErrorNode), init.isErrorNode);
                    binary.parentNode = lcVariableDeclaration;
                    this.semanticAnalyzer.referenceResolver.visitBinary(binary, additional);
                    this.visitBinary(binary, additional);
                    lcVariableDeclaration.init = binary;
                }
            }
        }

        if (lcVariableDeclaration.init != null) {
            Type t1 = lcVariableDeclaration.theType;
            Type t2 = this.getDynamicType(lcVariableDeclaration.init);

            if (t1 == null || t1.equals(SystemTypes.AUTO)) {
                t1 = t2;
                lcVariableDeclaration.theType = t2;
            } else if (t1 instanceof ReferenceType referenceType && referenceType.base.equals(SystemTypes.AUTO)) {
                referenceType.base = t2;
            }

            if (TypeUtil.LE(t2, t1)) {
                Type tRight = this.getDynamicType(lcVariableDeclaration.init);
                this.setExpressionType(this.makeVariableBySymbol(lcVariableDeclaration.symbol), tRight);

                if (this.options.get("traceTypeChecker", Boolean.class)) {
                    System.out.println("in lcVariableDecl '" + lcVariableDeclaration.name + "'");
                    this.dumpRange();
                }

                if (lcVariableDeclaration.typeExpression == null) {
                    lcVariableDeclaration.theType = tRight;
                    lcVariableDeclaration.symbol.theType = t2;
                }
            } else {
                System.err.println("Operator '=' can not be applied to '" + t1.toTypeString() + "' and '" + t2.toTypeString() + "'.");
            }
        } else if (lcVariableDeclaration.theType != null) {
            this.setExpressionType(this.makeVariableBySymbol(lcVariableDeclaration.symbol), lcVariableDeclaration.theType);
        }

        return null;
    }

    @Override
    public LCExpression visitBinary(LCBinary lcBinary, Object additional) {
        this.visit(lcBinary.expression1, additional);

        this.visit(lcBinary.expression2, additional);

        Type t1 = this.getDynamicType(lcBinary.expression1);
        Type t2 = this.getDynamicType(lcBinary.expression2);
        if (lcBinary._operator == Tokens.Operator.Dot) {

        } else if (lcBinary._operator == Tokens.Operator.MemberAccess) {

        } else if (Token.isAssignOperator(lcBinary._operator)) {
            if (lcBinary.expression2 instanceof LCNullLiteral nullLiteral && (nullLiteral.theType == null || nullLiteral.theType.equals(SystemTypes.AUTO))) {
                if (lcBinary.expression1.theType != null) {
                    nullLiteral.theType = lcBinary.expression1.theType;
                }
            }

            lcBinary.theType = t1;
            t2 = this.getDynamicType(lcBinary.expression2);

            if (t1 != null && t2 != null) {
                MethodSymbol methodSymbol = null;
                if (!SystemTypes.isPrimitiveType(t1) && lcBinary._operator != Tokens.Operator.Assign) {
                    String name = switch (lcBinary._operator) {
                        case PlusAssign -> "plusAssign";
                        case MinusAssign -> "minusAssign";
                        case MultiplyAssign -> "timesAssign";
                        case DivideAssign -> "divAssign";
                        case ModulusAssign -> "remAssign";
                        case LeftShiftArithmeticAssign -> "shlAssign";
                        case RightShiftArithmeticAssign -> "shrAssign";
                        case RightShiftLogicalAssign -> "ushrAssign";
                        case BitAndAssign -> "andAssign";
                        case BitOrAssign -> "orAssign";
                        case BitXorAssign -> "xorAssign";
                        default -> throw new IllegalStateException("Unexpected value: " + lcBinary._operator);
                    };
                    ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcBinary, t1.toTypeString()))));
                    methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t2});
                    if (methodSymbol == null) {
                        name = switch (lcBinary._operator) {
                            case PlusAssign -> "plus";
                            case MinusAssign -> "minus";
                            case MultiplyAssign -> "times";
                            case DivideAssign -> "div";
                            case ModulusAssign -> "rem";
                            case LeftShiftArithmeticAssign -> "shl";
                            case RightShiftArithmeticAssign -> "shr";
                            case RightShiftLogicalAssign -> "ushr";
                            case BitAndAssign -> "and";
                            case BitOrAssign -> "or";
                            case BitXorAssign -> "xor";
                            default -> throw new IllegalStateException("Unexpected value: " + lcBinary._operator);
                        };
                        methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t2});
                    }
                }
                if (methodSymbol != null) {
                    lcBinary.methodSymbol = methodSymbol;
                    lcBinary.theType = methodSymbol.returnType;
                } else if (TypeUtil.LE(t2, t1)) {
                    if (lcBinary.expression1 instanceof LCVariable) {
                    }

                    if (this.options.get("traceTypeChecker", Boolean.class)) {
                        System.out.println("in visitBinary, assignOP:");
                        this.dumpRange();
                    }
                } else if (t1 instanceof PointerType && SystemTypes.isIntegerType(t2)) {
                } else {
                    System.err.println("Can not assign '" + t2.toTypeString() + "' to '" + t1.toTypeString() + "'.");
                }
            }
        } else if (lcBinary._operator == Tokens.Operator.Plus || lcBinary._operator == Tokens.Operator.Minus) {
            if (t1 instanceof PointerType && SystemTypes.isIntegerType(t2)) {
                LCTypeCast lcTypeCast = new LCTypeCast(LCTypeCast.Kind.STATIC, new LCPointerTypeExpression(new LCPredefinedTypeExpression(Tokens.Type.Void, Position.origin), Position.origin), lcBinary.expression2, lcBinary.expression2.position, lcBinary.expression2.isErrorNode);
                lcBinary.expression2 = lcTypeCast;
                lcBinary.expression2.parentNode = lcBinary;
                lcTypeCast.theType = SystemTypes.VOID_POINTER;
            } else if (SystemTypes.isNumberType(t1)) {
                if (SystemTypes.isNumberType(t2)) {
                    lcBinary.theType = TypeUtil.getUpperBound(t1, t2);
                } else {
                    String name = lcBinary._operator == Tokens.Operator.Plus ? "plus" : "minus";
                    LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByFullName(lcBinary, t2.toTypeString());
                    System.err.println(name);
                    if (objectDeclaration != null) {
                        ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(objectDeclaration));
                        MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t1});
                        if (methodSymbol != null) {
                            lcBinary.methodSymbol = methodSymbol;
                            lcBinary.theType = methodSymbol.returnType;
                        } else {
                            System.err.println("Operator '" + lcBinary._operator.getCode() + "' can not be applied to '" + t1.toTypeString() + "' and '" + t2.toTypeString() + "'.");
                        }
                    } else {
                        lcBinary.theType = TypeUtil.getUpperBound(t1, t2);
                    }
                }
            } else {
                String name = lcBinary._operator == Tokens.Operator.Plus ? "plus" : "minus";
                LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByFullName(lcBinary, t1.toTypeString());
                if (objectDeclaration != null) {
                    ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(objectDeclaration));
                    MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t2});
                    if (methodSymbol != null) {
                        lcBinary.methodSymbol = methodSymbol;
                        lcBinary.theType = methodSymbol.returnType;
                    } else {
                        System.err.println("Operator '" + lcBinary._operator.getCode() + "' can not be applied to '" + t1.toTypeString() + "' and '" + t2.toTypeString() + "'.");
                    }
                } else {
                    lcBinary.theType = TypeUtil.getUpperBound(t1, t2);
                }
            }
        } else if (Token.isArithmeticOperator(lcBinary._operator)) {
            LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByFullName(lcBinary, t1.toTypeString());
            if (objectDeclaration != null) {
                String name = switch (lcBinary._operator) {
                    case Multiply -> "multiply";
                    case Divide -> "div";
                    case Modulus -> "rem";
                    case BitAnd -> "and";
                    case BitOr -> "or";
                    case BitXor -> "xor";
                    case LeftShiftArithmetic -> "shl";
                    case RightShiftArithmetic -> "shr";
                    case RightShiftLogical -> "ushr";
                    default -> throw new RuntimeException("Unreachable code");
                };
                ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(objectDeclaration));
                MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t2});
                if (methodSymbol != null) {
                    lcBinary.methodSymbol = methodSymbol;
                    lcBinary.theType = methodSymbol.returnType;
                } else {
                    lcBinary.theType = TypeUtil.getUpperBound(t1, t2);
                    if (!SystemTypes.isNumberType(t1) || !SystemTypes.isNumberType(t2)) {
                        System.err.println("Operator '" + lcBinary._operator.getCode() + "' can not be applied to '" + t1.toTypeString() + "' and '" + t2.toTypeString() + "'.");
                    }
                }
            } else {
                lcBinary.theType = TypeUtil.getUpperBound(t1, t2);
            }
        } else if (Token.isRelationOperator(lcBinary._operator)) {
            LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByFullName(lcBinary, t1.toTypeString());
            if (objectDeclaration != null) {
                String name = switch (lcBinary._operator) {
                    case Equal, NotEqual -> "equals";
                    case Less, LessEqual, Greater, GreaterEqual -> "compareTo";
                    default -> throw new RuntimeException("Unreachable code");
                };
                ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(objectDeclaration));
                MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[]{t2});
                if (methodSymbol != null) {
                    lcBinary.methodSymbol = methodSymbol;
                    lcBinary.theType = methodSymbol.returnType;
                } else {
                    lcBinary.theType = SystemTypes.BOOLEAN;
                    if (!TypeUtil.overlap(t1, t2) && !(SystemTypes.isNumberType(t1) && SystemTypes.isNumberType(t2))) {
                        System.err.println("Operator '" + lcBinary._operator + "' can not be applied to '" + t1 + "' and '" + t2.toString() + "'.");
                    }
                }
            } else {
                lcBinary.theType = SystemTypes.BOOLEAN;
            }
        } else if (Token.isLogicalOperator(lcBinary._operator)) {
            lcBinary.theType = SystemTypes.BOOLEAN;
            if (!SystemTypes.BOOLEAN.equals(t1)) {
                System.err.println("Operator '" + lcBinary._operator.getCode() + "' can not be applied to '" + t1.toTypeString() + "'.");
            } else if (!SystemTypes.BOOLEAN.equals(t2)) {
                System.err.println("Operator '" + lcBinary._operator.getCode() + "' can not be applied to '" + t2.toTypeString() + "'.");
            }
        } else {
            System.err.println("Unsupported binary operator: " + lcBinary._operator.getCode());
        }

        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object additional) {
        Object result = this.visit(lcUnary.expression, additional);
        if (result instanceof LCExpression expression) lcUnary.expression = expression;

        Type t = this.getDynamicType(lcUnary.expression);


        String name = switch (lcUnary._operator) {
            case Inc -> "inc";
            case Dec -> "dec";
            case Plus -> "unaryPlus";
            case Minus -> "unaryMinus";
            case Not -> "not";
            case BitNot -> "inv";
            default -> null;
        };
        if (name == null) {
            System.err.println("Unsupported unary operator: " + lcUnary._operator.getCode() + " applied to '" + t.toTypeString() + "'.");
        } else {
            LCObjectDeclaration objectDeclaration = LCAstUtil.getObjectDeclarationByFullName(lcUnary, t.toTypeString());
            if (objectDeclaration != null) {
                ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcUnary, t.toTypeString()))));
                MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, name, new Type[0]);
                if (methodSymbol != null) {
                    lcUnary.methodSymbol = methodSymbol;
                    lcUnary.theType = methodSymbol.returnType;
                } else {
                    if (lcUnary._operator == Tokens.Operator.Not && SystemTypes.BOOLEAN.equals(t)) {
                        lcUnary.theType = SystemTypes.BOOLEAN;
                    } else if (SystemTypes.isNumberType(t)) {
                        lcUnary.theType = t;
                    } else {
                        System.err.println("Unary operator " + lcUnary._operator.getCode() + " can not be applied to '" + t.toTypeString() + "'.");
                    }
                }
            } else {
                if (lcUnary._operator == Tokens.Operator.Not && SystemTypes.BOOLEAN.equals(t)) {
                    lcUnary.theType = SystemTypes.BOOLEAN;
                } else if (SystemTypes.isNumberType(t)) {
                    lcUnary.theType = t;
                } else {
                    System.err.println("Unary operator " + lcUnary._operator.getCode() + " can not be applied to '" + t.toTypeString() + "'.");
                }
            }
        }

        return null;
    }

    @Override
    public Object visitTypeof(LCTypeof lcTypeof, Object additional) {
        this.visit(lcTypeof.expression, additional);

        Type theType = this.getDynamicType(lcTypeof.expression);
        lcTypeof.constValue = new ConstValue(theType.toTypeString());
        lcTypeof.theType = SystemTypes.String_Type;

        return null;
    }

    @Override
    public Object visitVariable(LCVariable v, Object additional) {
        Object c = this.getVarConstValue(v.symbol);
        if (c != null) {
            v.constValue = new ConstValue(c);
        }
        return null;
    }

    @Override
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object additional) {
        this.setExpressionType(lcIntegerLiteral, lcIntegerLiteral.theType);
        return null;
    }

    @Override
    public LCExpression visitArrayAccess(LCArrayAccess lcArrayAccess, Object additional) {
        this.visit(lcArrayAccess.base, additional);

        Type t = lcArrayAccess.base.theType;
        if (t instanceof ArrayType arrayType) {
            lcArrayAccess.theType = arrayType.base;
        } else if (t instanceof PointerType pointerType) {
            lcArrayAccess.theType = pointerType.base;
        }

        this.visit(lcArrayAccess.index, additional);
        if (!SystemTypes.isNumberType(lcArrayAccess.index.theType)) {
            System.err.println("The index of array elements should be of type number.");
        }

        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        super.visitNewObject(lcNewObject, additional);
        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object additional) {
        super.visitMethodCall(lcMethodCall, additional);
        // TODO check method call
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        this.pushNewMaps();

        boolean lastInIfCondition = this.inIfCondition;
        this.inIfCondition = true;
        Object condition = this.visit(lcIf.condition, additional);
        if (condition instanceof LCExpression expression) lcIf.condition = expression;
        this.inIfCondition = lastInIfCondition;
        this.visit(lcIf.then, additional);

        if (lcIf._else != null) {
            this.visit(lcIf._else, additional);
        }
        if (lcIf.then instanceof LCExpressionStatement lcExpressionStatement) {
            lcIf.theType = lcExpressionStatement.expression.theType;
        }

        this.popMaps();
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        this.pushNewMaps();
        super.visitBlock(lcBlock, additional);
        this.popMaps();
        return null;
    }

    @Override
    public Object visitThrow(LCThrow lcThrow, Object additional) {
        super.visitThrow(lcThrow, additional);

        if (!TypeUtil.LE(lcThrow.expression.theType, SystemTypes.Throwable_Type)) {
            System.err.println("不兼容的类型: " + lcThrow.expression.theType.toTypeString() + "无法转换为" + SystemTypes.Throwable_Type.name);
        }

        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        super.visitSwitchExpression(lcSwitchExpression, additional);

        if (!lcSwitchExpression.cases.isEmpty()) {
            lcSwitchExpression.theType = lcSwitchExpression.cases.getFirst().theType;
        }

        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object additional) {
        super.visitCase(lcCase, additional);
        if (!lcCase.statements.isEmpty() && lcCase.statements.get(lcCase.statements.size() - 1) instanceof LCExpressionStatement lcExpressionStatement) {
            lcCase.theType = lcExpressionStatement.expression.theType;
        }
        return null;
    }

    @Override
    public Object visitInstanceof(LCInstanceof lcInstanceof, Object additional) {
        Object result = this.visit(lcInstanceof.expression, additional);
        if (result instanceof LCExpression expression) lcInstanceof.expression = expression;

        this.visit(lcInstanceof.typeExpression, additional);

        this.setExpressionType(lcInstanceof, SystemTypes.BOOLEAN);
        if (this.inIfCondition) {
            this.setExpressionType(lcInstanceof.expression, lcInstanceof.typeExpression.theType);
        }

        return null;
    }

    @Override
    public Object visitTernary(LCTernary lcTernary, Object additional) {
        super.visitTernary(lcTernary, additional);
        lcTernary.theType = TypeUtil.getUpperBound(lcTernary.then.theType, lcTernary._else.theType);
        return null;
    }
}
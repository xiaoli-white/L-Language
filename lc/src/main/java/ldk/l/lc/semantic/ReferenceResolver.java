package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.expression.LCMethodCall;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.semantic.types.*;
import ldk.l.lc.token.Token;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.MethodSymbol;
import ldk.l.lc.util.symbol.object.*;
import ldk.l.lc.util.symbol.Symbol;
import ldk.l.lc.util.symbol.VariableSymbol;

import java.util.*;

public final class ReferenceResolver extends LCAstVisitor {
    private final SemanticAnalyzer semanticAnalyzer;
    private final ErrorStream errorStream;
    private Scope scope = null;
    private LCMethodDeclaration currentMethodDeclaration = null;
    private final HashMap<Scope, HashMap<String, VariableSymbol>> declaredVarsMap = new HashMap<>();
    private final Stack<ObjectSymbol> objectSymbolStack = new Stack<>();

    public ReferenceResolver(SemanticAnalyzer semanticAnalyzer, ErrorStream errorStream) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.errorStream = errorStream;
    }

    // TODO Resolve calling methods with generic type parameters
    @Override
    public Object visitAst(LCAst ast, Object additional) {
        super.visitAst(ast, null);
        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSourceCodeFile.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());

        super.visitSourceCodeFile(lcSourceCodeFile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcClassDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitClassDeclaration(lcClassDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcInterfaceDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcEnumDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitEnumDeclaration(lcEnumDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object additional) {
        Type[] types = new Type[lcEnumFieldDeclaration.arguments.size()];
        for (int i = 0; i < lcEnumFieldDeclaration.arguments.size(); i++) {
            LCExpression expression = lcEnumFieldDeclaration.arguments.get(i);
            this.visit(expression, additional);
            types[i] = expression.theType;
        }

        lcEnumFieldDeclaration.symbol.constructor = findMethodSymbolOfObjectSymbol(lcEnumFieldDeclaration.symbol.enumSymbol, "<init>", types);

        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcRecordDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitRecordDeclaration(lcRecordDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcAnnotationDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitAnnotationDeclaration(lcAnnotationDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcAnnotationBody.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitAnnotationBody(lcAnnotationBody, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcFor.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitFor(lcFor, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcForeach.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitForeach(lcForeach, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcWhile.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitWhile(lcWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcDoWhile.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitDoWhile(lcDoWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcLoop.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitLoop(lcLoop, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSwitchStatement.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitSwitchStatement(lcSwitchStatement, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSwitchExpression.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitSwitchExpression(lcSwitchExpression, additional);

        if (!lcSwitchExpression.cases.isEmpty()) {
            lcSwitchExpression.theType = lcSwitchExpression.cases.get(0).theType;
        }

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcCase.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitCase(lcCase, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcNative.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitNative(lcNative, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSynchronized.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitSynchronized(lcSynchronized, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcIf.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitIf(lcIf, additional);

        if (lcIf.then instanceof LCExpressionStatement exp1 && lcIf._else instanceof LCExpressionStatement exp2) {
            lcIf.theType = TypeUtil.getUpperBound(exp1.expression.theType, exp2.expression.theType);
        } else {
            lcIf.theType = SystemTypes.VOID;
        }

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcTry.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitTry(lcTry, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcCatch.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitCatch(lcCatch, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcWith.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitWith(lcWith, additional);

        ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcWith, SystemTypes.Closeable_Type.toTypeString()))));
        MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, "close", new Type[0]);

        if (methodSymbol != null) {
            lcWith.methodSymbol = methodSymbol;
        } else {
            System.err.println("Can not found method symbol for 'close()'.");
        }

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object additional) {
        LCObjectDeclaration lcObjectDeclaration = this.getEnclosingObjectDeclaration(lcThis);
        if (lcObjectDeclaration != null) {
            ObjectSymbol symbol = LCAstUtil.getObjectSymbol(lcObjectDeclaration);
            if (symbol != null) {
                lcThis.symbol = symbol;
                lcThis.theType = symbol.theType;
            } else {
                System.err.println("Can not found object symbol for 'this'.");
            }
        } else {
            System.err.println("Keyword 'this' should be inside a object.");
        }

        return null;
    }

    @Override
    public Object visitSuper(LCSuper lcSuper, Object additional) {
        LCObjectDeclaration lcObjectDeclaration = this.getEnclosingObjectDeclaration(lcSuper);
        if (lcObjectDeclaration != null) {
            ObjectSymbol symbol = LCAstUtil.getObjectSymbol(lcObjectDeclaration);
            if (!this.objectSymbolStack.isEmpty()) {
                lcSuper.symbol = this.objectSymbolStack.pop();
            } else if (symbol instanceof ClassSymbol classSymbol) {
                lcSuper.symbol = classSymbol.extended;
            } else if (!(symbol instanceof AnnotationSymbol)) {
                // TODO dump error
            }
            if (lcSuper.symbol != null) {
                lcSuper.theType = lcSuper.symbol.theType;
            } else {
                System.err.println("Can not found object symbol for 'super'.");
            }
        } else {
            System.err.println("Keyword 'super' should be inside a object.");
        }
        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        Scope currentScope = this.scope;
        HashMap<String, VariableSymbol> declaredSymbols = this.declaredVarsMap.get(currentScope);
        Symbol symbol = currentScope.getSymbol(lcVariableDeclaration.name);
        if (symbol != null) {
            declaredSymbols.put(lcVariableDeclaration.name, (VariableSymbol) symbol);
        }

        super.visitVariableDeclaration(lcVariableDeclaration, null);

        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object notAcceptObjectSymbol) {
        if (lcVariable.symbol != null) return null;

        Scope scope;
        if ((notAcceptObjectSymbol instanceof Boolean boolean_value && boolean_value) || this.objectSymbolStack.isEmpty()) {
            scope = this.scope;
        } else {
            scope = LCAstUtil.getObjectScope(Objects.requireNonNull(LCAstUtil.getObjectDeclaration(this.objectSymbolStack.pop())));
        }
        VariableSymbol symbol = this.findVariableSymbolCascade(scope, lcVariable);
        if (symbol != null) {
            lcVariable.symbol = symbol;
            lcVariable.theType = symbol.theType;
        }
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        LCMethodDeclaration oldMethodDecl = this.currentMethodDeclaration;
        this.currentMethodDeclaration = lcMethodDeclaration;

        Scope oldScope = this.scope;
        this.scope = lcMethodDeclaration.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());

        super.visitMethodDeclaration(lcMethodDeclaration, additional);

        this.scope = oldScope;
        this.currentMethodDeclaration = oldMethodDecl;

        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcBlock.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());
        super.visitBlock(lcBlock, additional);

        // TODO 支持yield
        if (!lcBlock.statements.isEmpty() && lcBlock.statements.getLast() instanceof LCExpressionStatement expressionStatement) {
            lcBlock.theType = expressionStatement.expression.theType;
        } else {
            lcBlock.theType = SystemTypes.VOID;
        }

        this.scope = oldScope;

        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcLambda.scope;
        if (this.scope == null) {
            this.errorStream.printError(true, Position.origin, -1);
            return null;
        }

        this.declaredVarsMap.put(this.scope, new HashMap<>());

        super.visitLambda(lcLambda, additional);

        this.scope = oldScope;

        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object additional) {
        if (lcMethodCall.expression != null) {
            this.visit(lcMethodCall.expression, true);
        }
        for (LCExpression argument : lcMethodCall.arguments) {
            this.visit(argument, true);
        }

        if (lcMethodCall.expression == null) {
            List<Type> paramTypes = new ArrayList<>();
            for (LCExpression argument : lcMethodCall.arguments) {
                paramTypes.add(argument.theType);
            }
            MethodSymbol methodSymbol;
            if (this.objectSymbolStack.isEmpty()) {
                methodSymbol = findMethodSymbolCascade(this.scope, lcMethodCall.name, paramTypes.toArray(new Type[0]));
            } else {
                methodSymbol = findMethodSymbolOfObjectSymbol(this.objectSymbolStack.pop(), lcMethodCall.name, paramTypes.toArray(new Type[0]));
            }
            if (methodSymbol != null) {
                lcMethodCall.symbol = methodSymbol;
                lcMethodCall.theType = methodSymbol.declaration.returnType;
            } else {
                LCVariable variable = new LCVariable(lcMethodCall.name, lcMethodCall.positionOfName, lcMethodCall.isErrorNode);
                this.visitVariable(variable, additional);
                if (variable.symbol != null && variable.theType instanceof MethodPointerType methodPointerType) {
                    lcMethodCall.expression = variable;
                    lcMethodCall.theType = methodPointerType.returnType;
                } else {
                    System.err.println("Cannot found a method of calling method '" + lcMethodCall.name + "'.");
                }
            }
        } else {
            if (lcMethodCall.expression.theType instanceof MethodPointerType methodPointerType) {
                lcMethodCall.theType = methodPointerType.returnType;
            } else {
                System.err.println("The type of expression with method calling must be a method type.");
            }
        }
        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        if (lcNewObject.place != null) {
            this.visit(lcNewObject.place, additional);
        }
        this.visit(lcNewObject.typeExpression, additional);
        List<Type> paramTypes = new ArrayList<>();
        for (LCExpression argument : lcNewObject.arguments) {
            this.visit(argument, additional);
            paramTypes.add(argument.theType);
        }
        lcNewObject.constructorSymbol = findMethodSymbolOfObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcNewObject, lcNewObject.typeExpression.theType.toTypeString())))), "<init>", paramTypes.toArray(new Type[0]));
        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        if (lcBinary._operator == Tokens.Operator.Dot || lcBinary._operator == Tokens.Operator.MemberAccess) {
            this.visit(lcBinary.expression1, additional);
            if (lcBinary.expression1 instanceof LCVariable lcVariable && lcVariable.symbol == null) {
                LCTypeReferenceExpression lcTypeReferenceExpression = new LCTypeReferenceExpression(lcVariable.name, lcVariable.position, lcVariable.isErrorNode);
                lcTypeReferenceExpression.setTypeArgs(new ArrayList<>());
                lcTypeReferenceExpression.parentNode = lcBinary;
                lcBinary.expression1 = lcTypeReferenceExpression;
                this.semanticAnalyzer.typeResolver.visitTypeReferenceExpression(lcTypeReferenceExpression, additional);
                if (lcTypeReferenceExpression.theType == null) {
                    while (lcBinary.expression2 instanceof LCBinary binary && binary.expression1 instanceof LCVariable variable) {
                        Position position = new Position(lcTypeReferenceExpression.position.beginPos(), variable.position.endPos(), lcTypeReferenceExpression.position.beginLine(), variable.position.endLine(), lcTypeReferenceExpression.position.beginCol(), variable.position.endCol());
                        lcTypeReferenceExpression = new LCTypeReferenceExpression(lcTypeReferenceExpression.name + "." + variable.name, position, variable.isErrorNode || lcTypeReferenceExpression.isErrorNode);
                        lcTypeReferenceExpression.setTypeArgs(new ArrayList<>());
                        lcTypeReferenceExpression.parentNode = lcBinary;
                        lcBinary.expression1 = lcTypeReferenceExpression;
                        lcBinary.expression2 = binary.expression2;
                        this.semanticAnalyzer.typeResolver.visitTypeReferenceExpression(lcTypeReferenceExpression, additional);
                        if (lcTypeReferenceExpression.theType != null) break;
                    }
                }
                if (lcTypeReferenceExpression.theType == null) {
                    System.err.println("Cannot find a symbol of name: '" + lcVariable.name + "'");
                    return null;
                }
            }

            Type baseType = lcBinary.expression1.theType;
            if (baseType instanceof NullableType nullableType) baseType = nullableType.base;

            if (lcBinary._operator == Tokens.Operator.MemberAccess) {
                baseType = ((PointerType) baseType).base;
            }

            if (baseType instanceof ArrayType && lcBinary.expression2 instanceof LCVariable lcVariable && "length".equals(lcVariable.name)) {
                lcBinary.expression2.theType = SystemTypes.INT;
            } else {
                if (baseType instanceof NamedType namedType) {
                    LCAstNode node;
                    if (lcBinary.expression1 instanceof LCVariable lcVariable) {
                        node = lcVariable.symbol.declaration;
                    } else {
                        node = lcBinary.expression1;
                    }
                    this.objectSymbolStack.push(Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(this.getAST(node).getObjectDeclaration(namedType.name)))));
                }
                this.visit(lcBinary.expression2, null);
            }
            lcBinary.theType = lcBinary.expression2.theType;
        } else {
            super.visitBinary(lcBinary, additional);
            if (Token.isRelationOperator(lcBinary._operator) || Token.isLogicalOperator(lcBinary._operator)) {
                lcBinary.theType = SystemTypes.BOOLEAN;
            } else if (Token.isAssignOperator(lcBinary._operator)) {
                lcBinary.theType = lcBinary.expression1.theType;
            } else if (Token.isArithmeticOperator(lcBinary._operator)) {
                lcBinary.theType = TypeUtil.getUpperBound(lcBinary.expression1.theType, lcBinary.expression2.theType);
            }
        }
        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object additional) {
        this.visit(lcUnary.expression, additional);
        lcUnary.theType = lcUnary.expression.theType;
        return null;
    }

    @Override
    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object additional) {
        super.visitNotNullAssert(lcNotNullAssert, additional);
        if (lcNotNullAssert.base.theType instanceof NullableType nullableType) {
            lcNotNullAssert.theType = nullableType.base;
        } else {
            lcNotNullAssert.theType = lcNotNullAssert.base.theType;

            lcNotNullAssert.isErrorNode = true;
            System.err.println("The base of not-null assert is not nullable type.");
        }
        return null;
    }

    @Override
    public Object visitFree(LCFree lcFree, Object additional) {
        super.visitFree(lcFree, additional);
        lcFree.theType = lcFree.expression.theType;
        return null;
    }

    @Override
    public Object visitRealloc(LCRealloc lcRealloc, Object additional) {
        super.visitRealloc(lcRealloc, additional);
        lcRealloc.theType = lcRealloc.expression.theType;
        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object additional) {
        super.visitDereference(lcDereference, additional);

        if (lcDereference.expression.theType instanceof PointerType pointerType) {
            lcDereference.theType = pointerType.base;
        } else {
            System.err.println("Dereference expression should be a pointer type.");
        }

        return null;
    }

    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object additional) {
        super.visitGetAddress(lcGetAddress, additional);
        lcGetAddress.theType = new PointerType(lcGetAddress.expression.theType);
        return null;
    }

    @Override
    public Object visitTernary(LCTernary lcTernary, Object additional) {
        super.visitTernary(lcTernary, additional);

        lcTernary.theType = TypeUtil.getUpperBound(lcTernary.then.theType, lcTernary._else.theType);

        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object additional) {
        super.visitArrayAccess(lcArrayAccess, additional);

        if (lcArrayAccess.base.theType instanceof ArrayType arrayType) {
            lcArrayAccess.theType = arrayType.base;
        } else if (lcArrayAccess.base.theType instanceof PointerType pointerType) {
            lcArrayAccess.theType = pointerType.base;
        } else {
            System.err.println("The base of array access should be an array type.");
        }

        return null;
    }

    @Override
    public Object visitIn(LCIn lcIn, Object additional) {
        super.visitIn(lcIn, additional);

        ObjectSymbol objectSymbol = Objects.requireNonNull(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcIn, lcIn.expression2.theType.toTypeString()))));
        MethodSymbol methodSymbol = ReferenceResolver.findMethodSymbolOfObjectSymbol(objectSymbol, "contains", new Type[]{lcIn.expression1.theType});

        if (methodSymbol != null) {
            lcIn.symbol = methodSymbol;
            lcIn.theType = methodSymbol.returnType;
        } else {
            System.err.println("Cannot find a method of name: 'contains'.");
        }

        return null;
    }

    @Override
    public Object visitAnnotation(LCAnnotation lcAnnotation, Object additional) {
        super.visitAnnotation(lcAnnotation, additional);

        ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcAnnotation, lcAnnotation.name)));
        if (objectSymbol instanceof AnnotationSymbol annotationSymbol) {
            lcAnnotation.symbol = annotationSymbol;
        } else {
            // TODO dump error
        }

        return null;
    }

    private VariableSymbol findVariableSymbolCascade(Scope scope, LCVariable lcVariable) {
        if (scope == null) return null;

        HashMap<String, VariableSymbol> declaredSymbols = this.declaredVarsMap.getOrDefault(scope, null);
        Symbol symbolInScope = scope.getSymbol(lcVariable.name);
        if (symbolInScope != null) {
            if (declaredSymbols != null && declaredSymbols.containsKey(lcVariable.name)) {
                return declaredSymbols.get(lcVariable.name);
            } else if (symbolInScope instanceof VariableSymbol variableSymbol) {
                if (variableSymbol.objectSymbol == null) {
                    System.err.println("The variable '" + lcVariable.name + "' is used before declaration.");
                }
                return variableSymbol;
            }
        }

        if (scope.enclosingScope != null) {
            return this.findVariableSymbolCascade(scope.enclosingScope, lcVariable);
        } else {
            return null;
        }
    }

    private static MethodSymbol findMethodSymbolCascade(Scope scope, String name, Type[] paramTypes) {
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof MethodSymbol methodSymbol) {
                if (methodSymbol.name.equals(name) && checkTypesOfMethod(methodSymbol, paramTypes))
                    return methodSymbol;
            }
        }
        if (scope.enclosingScope != null) {
            return findMethodSymbolCascade(scope.enclosingScope, name, paramTypes);
        }
        return null;
    }

    public static MethodSymbol findMethodSymbolOfObjectSymbol(ObjectSymbol objectSymbol, String name, Type[] paramTypes) {
        switch (objectSymbol) {
            case ClassSymbol classSymbol -> {
                if ("<deinit>".equals(name)) {
                    if (paramTypes.length == 0) {
                        return classSymbol.destructor;
                    } else {
                        // TODO dump error
                    }
                }
                List<MethodSymbol> methods = "<init>".equals(name) ? classSymbol.constructors : classSymbol.methods;
                MethodSymbol methodSymbol = null;
                for (MethodSymbol method : methods) {
                    if (method.name.equals(name) && checkTypesOfMethod(method, paramTypes)) {
                        if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                            methodSymbol = method;
                        }
                    }
                }
                if (methodSymbol != null) return methodSymbol;

                if (classSymbol.extended != null) {
                    methodSymbol = findMethodSymbolOfObjectSymbol(classSymbol.extended, name, paramTypes);
                    if (methodSymbol != null) return methodSymbol;
                }

                for (InterfaceSymbol interfaceSymbol : classSymbol.implementedInterfaces) {
                    MethodSymbol method = findMethodSymbolOfObjectSymbol(interfaceSymbol, name, paramTypes);
                    if (method == null) continue;
                    if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                        methodSymbol = method;
                    }
                }
                return methodSymbol;
            }
            case EnumSymbol enumSymbol -> {
                if ("<deinit>".equals(name)) {
                    if (paramTypes.length == 0) {
                        return enumSymbol.destructor;
                    } else {
                        // TODO dump error
                    }
                }
                List<MethodSymbol> methods = "<init>".equals(name) ? enumSymbol.constructors : enumSymbol.methods;
                MethodSymbol methodSymbol = null;
                for (MethodSymbol method : methods) {
                    if (method.name.equals(name) && checkTypesOfMethod(method, paramTypes)) {
                        if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                            methodSymbol = method;
                        }
                    }
                }
                if (methodSymbol != null) return methodSymbol;

                for (InterfaceSymbol interfaceSymbol : enumSymbol.implementedInterfaces) {
                    MethodSymbol method = findMethodSymbolOfObjectSymbol(interfaceSymbol, name, paramTypes);
                    if (method == null) continue;
                    if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                        methodSymbol = method;
                    }
                }
                return methodSymbol;
            }
            case InterfaceSymbol interfaceSymbol -> {
                MethodSymbol methodSymbol = null;
                for (MethodSymbol method : interfaceSymbol.methods) {
                    if (method.name.equals(name) && checkTypesOfMethod(method, paramTypes)) {
                        if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                            methodSymbol = method;
                        }
                    }
                }
                if (methodSymbol != null) return methodSymbol;

                for (InterfaceSymbol extended : interfaceSymbol.extendedInterfaces) {
                    MethodSymbol method = findMethodSymbolOfObjectSymbol(extended, name, paramTypes);
                    if (method == null) continue;
                    if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                        methodSymbol = method;
                    }
                }
                return methodSymbol;
            }
            case AnnotationSymbol annotationSymbol -> {
            }
            case RecordSymbol recordSymbol -> {
                if ("<deinit>".equals(name)) {
                    if (paramTypes.length == 0) {
                        return recordSymbol.destructor;
                    } else {
                        // TODO dump error
                    }
                }
                List<MethodSymbol> methods = "<init>".equals(name) ? recordSymbol.constructors : recordSymbol.methods;
                MethodSymbol methodSymbol = null;
                for (MethodSymbol method : methods) {
                    if (method.name.equals(name) && checkTypesOfMethod(method, paramTypes)) {
                        if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                            methodSymbol = method;
                        }
                    }
                }
                if (methodSymbol != null) return methodSymbol;

                for (InterfaceSymbol interfaceSymbol : recordSymbol.implementedInterfaces) {
                    MethodSymbol method = findMethodSymbolOfObjectSymbol(interfaceSymbol, name, paramTypes);
                    if (method == null) continue;
                    if (methodSymbol == null || isSubMethod(method, methodSymbol)) {
                        methodSymbol = method;
                    }
                }
                return methodSymbol;
            }
        }
        return null;
    }

    private static boolean checkTypesOfMethod(MethodSymbol methodSymbol, Type[] parameterTypes) {
        if (methodSymbol.declaration.parameterList.parameters.size() != parameterTypes.length)
            return false;
        for (int i = 0; i < parameterTypes.length; i++) {
            Type parameterTypeOfMethod = methodSymbol.declaration.parameterList.parameters.get(i).theType;
            if (parameterTypeOfMethod == null || !TypeUtil.LE(parameterTypes[i], parameterTypeOfMethod))
                return false;
        }
        return true;
    }

    private static boolean isSubMethod(MethodSymbol method1, MethodSymbol method2) {
        List<LCVariableDeclaration> method1Params = method1.getParams();
        List<LCVariableDeclaration> method2Params = method2.getParams();
        int count = 0;
        for (int i = 0; i < method2Params.size(); i++) {
            if (method1Params.get(i).theType.equals(method2Params.get(i).theType)) count++;
            else if (!TypeUtil.LE(method2Params.get(i).theType, method1Params.get(i).theType)) return false;
        }
        return count < method1Params.size();
    }
}
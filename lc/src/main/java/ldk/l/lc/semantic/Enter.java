package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.semantic.types.MethodPointerType;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.*;
import ldk.l.lc.util.symbol.object.*;

import java.util.ArrayList;
import java.util.List;

public final class Enter extends LCAstVisitor {
    private final ErrorStream errorStream;
    private Scope scope = null;
    private MethodSymbol currentMethodSymbol = null;
    private LCInit currentInit = null;

    public Enter(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        lcSourceCodeFile.scope = new Scope(lcSourceCodeFile, null);
        this.scope = lcSourceCodeFile.scope;
        return super.visitSourceCodeFile(lcSourceCodeFile, additional);
    }

    @Override
    public LCClassDeclaration visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        Scope currentScope = this.scope;
        Scope scope = new Scope(lcClassDeclaration, currentScope);
        lcClassDeclaration.scope = scope;
        this.scope = scope;

        super.visitClassDeclaration(lcClassDeclaration, additional);

        if (currentScope.hasSymbol(lcClassDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcClassDeclaration.name);
        }

        LCAst ast = this.getAST(lcClassDeclaration);

        ArrayList<TemplateTypeParameterSymbol> templateTypeParameters = new ArrayList<>();
        ArrayList<TypeParameterSymbol> typeParameters = new ArrayList<>();
        ArrayList<VariableSymbol> props = new ArrayList<>();
        ArrayList<MethodSymbol> constructors = new ArrayList<>();
        ArrayList<MethodSymbol> methods = new ArrayList<>();
        MethodSymbol destructor = null;

        Scope classScope = lcClassDeclaration.body.scope;
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof TypeParameterSymbol typeParameterSymbol) {
                typeParameters.add(typeParameterSymbol);
            } else if (symbol instanceof TemplateTypeParameterSymbol templateTypeParameterSymbol) {
                templateTypeParameters.add(templateTypeParameterSymbol);
            }
        }
        for (Symbol symbol : classScope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                props.add(variableSymbol);
            } else if (symbol instanceof MethodSymbol methodSymbol) {
                if (methodSymbol.methodKind == MethodKind.Constructor) {
                    constructors.add(methodSymbol);
                } else if (methodSymbol.methodKind == MethodKind.Destructor) {
                    destructor = methodSymbol;
                } else {
                    methods.add(methodSymbol);
                }
            }
        }

        ClassSymbol classSymbol = new ClassSymbol(lcClassDeclaration, ast.getType(lcClassDeclaration.getFullName()), templateTypeParameters.toArray(new TemplateTypeParameterSymbol[0]), typeParameters.toArray(new TypeParameterSymbol[0]), lcClassDeclaration.modifier.flags, lcClassDeclaration.modifier.attributes, props.toArray(new VariableSymbol[0]), constructors.toArray(new MethodSymbol[0]), methods.toArray(new MethodSymbol[0]), destructor);
        lcClassDeclaration.symbol = classSymbol;
        currentScope.enter(lcClassDeclaration.name, classSymbol);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        Scope currentScope = this.scope;
        Scope scope = new Scope(lcInterfaceDeclaration, currentScope);
        lcInterfaceDeclaration.scope = scope;
        this.scope = scope;

        super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);

        if (currentScope.hasSymbol(lcInterfaceDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcInterfaceDeclaration.name);
        }

        LCAst ast = this.getAST(lcInterfaceDeclaration);

        ArrayList<TemplateTypeParameterSymbol> templateTypeParameters = new ArrayList<>();
        ArrayList<TypeParameterSymbol> typeParameters = new ArrayList<>();
        ArrayList<MethodSymbol> methods = new ArrayList<>();

        Scope interfaceScope = lcInterfaceDeclaration.body.scope;
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof TypeParameterSymbol typeParameterSymbol) {
                typeParameters.add(typeParameterSymbol);
            } else if (symbol instanceof TemplateTypeParameterSymbol templateTypeParameterSymbol) {
                templateTypeParameters.add(templateTypeParameterSymbol);
            }
        }
        for (Symbol symbol : interfaceScope.name2symbol.values()) {
            if (symbol instanceof MethodSymbol methodSymbol) {
                methods.add(methodSymbol);
            }
        }

        InterfaceSymbol interfaceSymbol = new InterfaceSymbol(lcInterfaceDeclaration, ast.getType(lcInterfaceDeclaration.getFullName()), templateTypeParameters.toArray(new TemplateTypeParameterSymbol[0]), typeParameters.toArray(new TypeParameterSymbol[0]), lcInterfaceDeclaration.modifier.flags, lcInterfaceDeclaration.modifier.attributes, methods.toArray(new MethodSymbol[0]));
        lcInterfaceDeclaration.symbol = interfaceSymbol;
        currentScope.enter(lcInterfaceDeclaration.name, interfaceSymbol);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        Scope currentScope = this.scope;
        Scope scope = new Scope(lcEnumDeclaration, currentScope);
        lcEnumDeclaration.scope = scope;
        this.scope = scope;

        super.visitEnumDeclaration(lcEnumDeclaration, additional);

        if (currentScope.hasSymbol(lcEnumDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcEnumDeclaration.name);
        }

        LCAst ast = this.getAST(lcEnumDeclaration);

        ArrayList<TemplateTypeParameterSymbol> templateTypeParameters = new ArrayList<>();
        ArrayList<TypeParameterSymbol> typeParameters = new ArrayList<>();
        ArrayList<EnumSymbol.EnumFieldSymbol> fields = new ArrayList<>();
        ArrayList<VariableSymbol> props = new ArrayList<>();
        ArrayList<MethodSymbol> constructors = new ArrayList<>();
        ArrayList<MethodSymbol> methods = new ArrayList<>();
        MethodSymbol destructor = null;

        Scope enumScope = lcEnumDeclaration.body.scope;
        for (Symbol symbol : lcEnumDeclaration.scope.name2symbol.values()) {
            if (symbol instanceof EnumSymbol.EnumFieldSymbol enumFieldSymbol) {
                fields.add(enumFieldSymbol);
            } else if (symbol instanceof TypeParameterSymbol typeParameterSymbol) {
                typeParameters.add(typeParameterSymbol);
            } else if (symbol instanceof TemplateTypeParameterSymbol templateTypeParameterSymbol) {
                templateTypeParameters.add(templateTypeParameterSymbol);
            }
        }
        for (Symbol symbol : enumScope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                props.add(variableSymbol);
            } else if (symbol instanceof MethodSymbol methodSymbol) {
                if (methodSymbol.methodKind == MethodKind.Constructor) {
                    constructors.add(methodSymbol);
                } else if (methodSymbol.methodKind == MethodKind.Destructor) {
                    destructor = methodSymbol;
                } else {
                    methods.add(methodSymbol);
                }
            }
        }

        EnumSymbol enumSymbol = new EnumSymbol(lcEnumDeclaration, ast.getType(lcEnumDeclaration.getFullName()), templateTypeParameters.toArray(new TemplateTypeParameterSymbol[0]), typeParameters.toArray(new TypeParameterSymbol[0]), lcEnumDeclaration.modifier.flags, lcEnumDeclaration.modifier.attributes, fields.toArray(new EnumSymbol.EnumFieldSymbol[0]), props.toArray(new VariableSymbol[0]), constructors.toArray(new MethodSymbol[0]), methods.toArray(new MethodSymbol[0]), destructor);
        lcEnumDeclaration.symbol = enumSymbol;
        currentScope.enter(lcEnumDeclaration.name, enumSymbol);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object additional) {
        Type theType;
        if (lcEnumFieldDeclaration.parentNode instanceof LCEnumDeclaration enumDeclaration) {
            theType = this.getAST(lcEnumFieldDeclaration).getType(enumDeclaration.getFullName());
        } else {
            theType = null;
        }
        EnumSymbol.EnumFieldSymbol enumFieldSymbol = new EnumSymbol.EnumFieldSymbol(lcEnumFieldDeclaration, theType);
        lcEnumFieldDeclaration.symbol = enumFieldSymbol;
        this.scope.enter(lcEnumFieldDeclaration.name, enumFieldSymbol);
        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        Scope currentScope = this.scope;
        Scope scope = new Scope(lcAnnotationDeclaration, currentScope);
        lcAnnotationDeclaration.scope = scope;
        this.scope = scope;

        super.visitAnnotationDeclaration(lcAnnotationDeclaration, additional);

        if (currentScope.hasSymbol(lcAnnotationDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcAnnotationDeclaration.name);
        }

        LCAst ast = this.getAST(lcAnnotationDeclaration);

        ArrayList<TemplateTypeParameterSymbol> templateTypeParameters = new ArrayList<>();
        ArrayList<TypeParameterSymbol> typeParameters = new ArrayList<>();
        ArrayList<AnnotationSymbol.AnnotationFieldSymbol> fields = new ArrayList<>();

        Scope annotationScope = lcAnnotationDeclaration.annotationBody.scope;
        for (Symbol symbol : scope.name2symbol.values()) {
            if (symbol instanceof TypeParameterSymbol typeParameterSymbol) {
                typeParameters.add(typeParameterSymbol);
            } else if (symbol instanceof TemplateTypeParameterSymbol templateTypeParameterSymbol) {
                templateTypeParameters.add(templateTypeParameterSymbol);
            }
        }
        for (Symbol symbol : annotationScope.name2symbol.values()) {
            if (symbol instanceof AnnotationSymbol.AnnotationFieldSymbol annotationFieldSymbol) {
                fields.add(annotationFieldSymbol);
            }
        }

        AnnotationSymbol annotationSymbol = new AnnotationSymbol(lcAnnotationDeclaration, ast.getType(lcAnnotationDeclaration.getFullName()), templateTypeParameters.toArray(new TemplateTypeParameterSymbol[0]), typeParameters.toArray(new TypeParameterSymbol[0]), lcAnnotationDeclaration.modifier.flags, lcAnnotationDeclaration.modifier.attributes, fields.toArray(new AnnotationSymbol.AnnotationFieldSymbol[0]));
        lcAnnotationDeclaration.symbol = annotationSymbol;
        currentScope.enter(lcAnnotationDeclaration.name, annotationSymbol);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object additional) {
        Scope currentScope = this.scope;

        Scope scope = new Scope(lcAnnotationBody, currentScope);
        lcAnnotationBody.scope = scope;
        this.scope = scope;

        super.visitAnnotationBody(lcAnnotationBody, additional);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitAnnotationFieldDeclaration(LCAnnotationDeclaration.LCAnnotationFieldDeclaration lcAnnotationFieldDeclaration, Object additional) {
        AnnotationSymbol.AnnotationFieldSymbol annotationFieldSymbol = new AnnotationSymbol.AnnotationFieldSymbol(lcAnnotationFieldDeclaration, lcAnnotationFieldDeclaration.typeExpression.theType);
        lcAnnotationFieldDeclaration.symbol = annotationFieldSymbol;
        this.scope.enter(lcAnnotationFieldDeclaration.name, annotationFieldSymbol);
        return super.visitAnnotationFieldDeclaration(lcAnnotationFieldDeclaration, additional);
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        Scope currentScope = this.scope;

        Scope recordScope = new Scope(lcRecordDeclaration, currentScope);
        lcRecordDeclaration.scope = recordScope;
        this.scope = recordScope;
        super.visitRecordDeclaration(lcRecordDeclaration, additional);

        if (currentScope.hasSymbol(lcRecordDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcRecordDeclaration.name);
        }

        LCAst ast = this.getAST(lcRecordDeclaration);

        ArrayList<TemplateTypeParameterSymbol> templateTypeParameters = new ArrayList<>();
        ArrayList<TypeParameterSymbol> typeParameters = new ArrayList<>();
        ArrayList<VariableSymbol> fields = new ArrayList<>();
        ArrayList<VariableSymbol> props = new ArrayList<>();
        ArrayList<MethodSymbol> constructors = new ArrayList<>();
        ArrayList<MethodSymbol> methods = new ArrayList<>();
        MethodSymbol destructor = null;

        for (Symbol symbol : recordScope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                fields.add(variableSymbol);
            } else if (symbol instanceof TypeParameterSymbol typeParameterSymbol) {
                typeParameters.add(typeParameterSymbol);
            } else if (symbol instanceof TemplateTypeParameterSymbol templateTypeParameterSymbol) {
                templateTypeParameters.add(templateTypeParameterSymbol);
            }
        }
        Scope recordBodyScope = lcRecordDeclaration.body.scope;
        for (Symbol symbol : recordBodyScope.name2symbol.values()) {
            if (symbol instanceof VariableSymbol variableSymbol) {
                props.add(variableSymbol);
            } else if (symbol instanceof MethodSymbol methodSymbol) {
                if (methodSymbol.methodKind == MethodKind.Constructor) {
                    constructors.add(methodSymbol);
                } else if (methodSymbol.methodKind == MethodKind.Destructor) {
                    destructor = methodSymbol;
                } else {
                    methods.add(methodSymbol);
                }
            }
        }

        RecordSymbol recordSymbol = new RecordSymbol(lcRecordDeclaration, ast.getType(lcRecordDeclaration.getFullName()), templateTypeParameters.toArray(new TemplateTypeParameterSymbol[0]), typeParameters.toArray(new TypeParameterSymbol[0]), lcRecordDeclaration.modifier.flags, lcRecordDeclaration.modifier.attributes, fields.toArray(new VariableSymbol[0]), props.toArray(new VariableSymbol[0]), constructors.toArray(new MethodSymbol[0]), methods.toArray(new MethodSymbol[0]), destructor);
        lcRecordDeclaration.symbol = recordSymbol;
        currentScope.enter(lcRecordDeclaration.name, recordSymbol);

        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        Scope currentScope = this.scope;

        if (lcMethodDeclaration.methodKind == MethodKind.Constructor || lcMethodDeclaration.methodKind == MethodKind.Destructor) {
            lcMethodDeclaration.returnType = SystemTypes.VOID;
        }

        List<Type> paramTypes = new ArrayList<>();
        if (lcMethodDeclaration.callSignature.parameterList != null) {
            for (LCVariableDeclaration p : lcMethodDeclaration.callSignature.parameterList.parameters)
                paramTypes.add(p.theType);
        }
        MethodSymbol symbol = new MethodSymbol(lcMethodDeclaration.name, new ArrayList<>(paramTypes), lcMethodDeclaration.returnType, new MethodPointerType(paramTypes.toArray(new Type[0]), lcMethodDeclaration.returnType), lcMethodDeclaration.methodKind, lcMethodDeclaration.modifier.flags, lcMethodDeclaration.modifier.attributes);
        symbol.declaration = lcMethodDeclaration;
        lcMethodDeclaration.symbol = symbol;

        String name = symbol.getSimpleName();
        if (currentScope.hasSymbol(name)) {
            System.err.println("Duplicate symbol: " + name);
        } else {
            currentScope.enter(name, symbol);
        }

        MethodSymbol lastMethodSymbol = this.currentMethodSymbol;
        this.currentMethodSymbol = symbol;

        this.scope = new Scope(lcMethodDeclaration, currentScope);
        lcMethodDeclaration.scope = this.scope;

        super.visitMethodDeclaration(lcMethodDeclaration, additional);

        this.currentMethodSymbol = lastMethodSymbol;
        this.scope = currentScope;

        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        Scope oldScope = this.scope;
        this.scope = new Scope(lcBlock, this.scope);
        lcBlock.scope = this.scope;

        super.visitBlock(lcBlock, additional);

        this.scope = oldScope;

        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        Scope currentScope = this.scope;
        if (currentScope.hasSymbol(lcVariableDeclaration.name)) {
            System.err.println("Duplicate symbol: " + lcVariableDeclaration.name);
        }

        super.visitVariableDeclaration(lcVariableDeclaration, additional);

        VariableSymbol symbol = new VariableSymbol(lcVariableDeclaration, lcVariableDeclaration.theType, lcVariableDeclaration.modifier.flags, lcVariableDeclaration.modifier.attributes);
        symbol.declaration = lcVariableDeclaration;

        lcVariableDeclaration.symbol = symbol;
        currentScope.enter(lcVariableDeclaration.name, symbol);

        if (this.currentMethodSymbol != null) {
            this.currentMethodSymbol.vars.add(symbol);
        } else if (this.currentInit != null) {
            this.currentInit.vars.add(symbol);
        }

        return null;
    }

    @Override
    public LCFor visitFor(LCFor lcFor, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcFor, this.scope);
        this.scope = scope;
        lcFor.scope = scope;

        super.visitFor(lcFor, null);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcForeach, this.scope);
        lcForeach.scope = scope;
        this.scope = scope;

        super.visitForeach(lcForeach, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcWhile, this.scope);
        lcWhile.scope = scope;
        this.scope = scope;

        super.visitWhile(lcWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcDoWhile, this.scope);
        lcDoWhile.scope = scope;
        this.scope = scope;

        super.visitDoWhile(lcDoWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcLoop, this.scope);
        lcLoop.scope = scope;
        this.scope = scope;

        super.visitLoop(lcLoop, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcSwitchStatement, this.scope);
        lcSwitchStatement.scope = scope;
        this.scope = scope;

        super.visitSwitchStatement(lcSwitchStatement, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcNative, this.scope);
        lcNative.scope = scope;
        this.scope = scope;

        super.visitNative(lcNative, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitResourceForNative(LCNative.LCResourceForNative lcResourceForNative, Object additional) {
        super.visitResourceForNative(lcResourceForNative, additional);
        this.scope.enter(lcResourceForNative.name, new ResourceForNativeSymbol(lcResourceForNative.name, lcResourceForNative.resource.theType));
        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object additional) {
        Scope currentScope = this.scope;

        ArrayList<Type> paramTypes = new ArrayList<>();
        if (lcLambda.callSignature.parameterList != null) {
            for (LCVariableDeclaration p : lcLambda.callSignature.parameterList.parameters)
                paramTypes.add(p.theType);
        }
        MethodSymbol symbol = new MethodSymbol(new ArrayList<>(paramTypes), lcLambda.returnType, new MethodPointerType(paramTypes.toArray(new Type[0]), lcLambda.returnType), MethodKind.Method, lcLambda.modifier.flags, lcLambda.modifier.attributes);
        symbol.lambdaDeclaration = lcLambda;
        lcLambda.symbol = symbol;

        currentScope.enter(symbol.getSimpleName(), symbol);

        MethodSymbol lastMethodSymbol = this.currentMethodSymbol;
        this.currentMethodSymbol = symbol;

        Scope scope = new Scope(lcLambda, currentScope);
        lcLambda.scope = scope;
        this.scope = scope;

        super.visitLambda(lcLambda, additional);

        this.currentMethodSymbol = lastMethodSymbol;
        this.scope = currentScope;
        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcCase, this.scope);
        lcCase.scope = scope;
        this.scope = scope;

        super.visitCase(lcCase, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcSwitchExpression, this.scope);
        lcSwitchExpression.scope = scope;
        this.scope = scope;

        super.visitSwitchExpression(lcSwitchExpression, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcSynchronized, this.scope);
        lcSynchronized.scope = scope;
        this.scope = scope;

        super.visitSynchronized(lcSynchronized, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcIf, this.scope);
        lcIf.scope = scope;
        this.scope = scope;

        super.visitIf(lcIf, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcTry, this.scope);
        lcTry.scope = scope;
        this.scope = scope;

        super.visitTry(lcTry, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcCatch, this.scope);
        lcCatch.scope = scope;
        this.scope = scope;

        super.visitCatch(lcCatch, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object additional) {
        Scope oldScope = this.scope;
        Scope scope = new Scope(lcWith, this.scope);
        lcWith.scope = scope;
        this.scope = scope;

        super.visitWith(lcWith, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitInit(LCInit lcInit, Object additional) {
        LCInit lastInit = this.currentInit;
        this.currentInit = lcInit;
        super.visitInit(lcInit, additional);
        this.currentInit = lastInit;
        return null;
    }
}
package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.semantic.types.NamedType;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.TypeParameterSymbol;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.ArrayList;
import java.util.Objects;

public final class TypeParameterEnter extends LCAstVisitor {
    private final ErrorStream errorStream;
    private Scope scope = null;

    public TypeParameterEnter(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitTypeParameter(LCTypeParameter lcTypeParameter, Object additional) {
        ObjectSymbol extended;
        if (lcTypeParameter.extended != null && lcTypeParameter.extended.theType instanceof NamedType namedType) {
            extended = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcTypeParameter.extended, namedType.name)));
        } else {
            extended = null;
        }
        ArrayList<ObjectSymbol> implemented = new ArrayList<>();
        for (LCTypeReferenceExpression type : lcTypeParameter.implemented) {
            if (type.theType instanceof NamedType namedType)
                implemented.add(LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(type, namedType.name))));
        }
        ObjectSymbol supered;
        if (lcTypeParameter.supered != null && lcTypeParameter.supered.theType instanceof NamedType namedType) {
            supered = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcTypeParameter.supered, namedType.name)));
        } else {
            supered = null;
        }
        ObjectSymbol _default;
        if (lcTypeParameter._default != null && lcTypeParameter._default.theType instanceof NamedType namedType) {
            _default = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByFullName(lcTypeParameter._default, namedType.name)));
        } else {
            _default = null;
        }
        TypeParameterSymbol symbol = new TypeParameterSymbol(lcTypeParameter, extended, implemented.toArray(new ObjectSymbol[0]), supered, _default);
        lcTypeParameter.symbol = symbol;
        this.scope.enter(lcTypeParameter.name, symbol);
        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSourceCodeFile.scope;

        super.visitSourceCodeFile(lcSourceCodeFile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcClassDeclaration.scope;

        super.visitClassDeclaration(lcClassDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcInterfaceDeclaration.scope;

        super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcEnumDeclaration.scope;

        super.visitEnumDeclaration(lcEnumDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcRecordDeclaration.scope;

        super.visitRecordDeclaration(lcRecordDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcAnnotationDeclaration.scope;

        super.visitAnnotationDeclaration(lcAnnotationDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcAnnotationBody.scope;

        super.visitAnnotationBody(lcAnnotationBody, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitStructDeclaration(LCStructDeclaration lcStructDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcStructDeclaration.scope;

        super.visitStructDeclaration(lcStructDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcMethodDeclaration.scope;

        super.visitMethodDeclaration(lcMethodDeclaration, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcFor.scope;

        super.visitFor(lcFor, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcForeach.scope;

        super.visitForeach(lcForeach, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcWhile.scope;

        super.visitWhile(lcWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcDoWhile.scope;

        super.visitDoWhile(lcDoWhile, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcLoop.scope;

        super.visitLoop(lcLoop, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcLambda.scope;

        super.visitLambda(lcLambda, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcBlock.scope;

        super.visitBlock(lcBlock, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSwitchStatement.scope;

        super.visitSwitchStatement(lcSwitchStatement, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSwitchExpression.scope;

        super.visitSwitchExpression(lcSwitchExpression, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcCase.scope;

        super.visitCase(lcCase, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcNative.scope;

        super.visitNative(lcNative, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcSynchronized.scope;

        super.visitSynchronized(lcSynchronized, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcIf.scope;

        super.visitIf(lcIf, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcTry.scope;

        super.visitTry(lcTry, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcCatch.scope;

        super.visitCatch(lcCatch, additional);

        this.scope = oldScope;
        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object additional) {
        Scope oldScope = this.scope;
        this.scope = lcWith.scope;

        super.visitWith(lcWith, additional);

        this.scope = oldScope;
        return null;
    }
}

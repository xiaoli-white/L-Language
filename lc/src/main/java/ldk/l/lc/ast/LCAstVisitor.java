package ldk.l.lc.ast;

import ldk.l.lc.ast.expression.literal.*;
import ldk.l.lc.ast.expression.type.*;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.LCNewArray;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.ast.file.LCSourceFileProxy;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.expression.LCMethodCall;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.util.scope.Scope;

public abstract class LCAstVisitor {
    public Object visit(LCAstNode node, Object additional) {
        return node.accept(this, additional);
    }

    public final LCAst getAST(LCAstNode node) {
        while (node.parentNode != null) {
            node = node.parentNode;
        }
        return (LCAst) node;
    }

    public LCMethodDeclaration getEnclosingMethodDeclaration(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCMethodDeclaration) {
                return (LCMethodDeclaration) parent;
            } else if (parent instanceof LCObjectDeclaration) {
                return null;
            } else {
                parent = parent.parentNode;
            }
        }
        return null;
    }

    public LCInit getEnclosingInit(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCInit) {
                return (LCInit) parent;
            } else if (parent instanceof LCObjectDeclaration) {
                return null;
            } else {
                parent = parent.parentNode;
            }
        }
        return null;
    }

    public LCObjectDeclaration getEnclosingObjectDeclaration(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCObjectDeclaration) {
                return (LCObjectDeclaration) parent;
            } else {
                parent = parent.parentNode;
            }
        }
        return null;
    }

    public LCAbstractLoop getEnclosingLoop(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCAbstractLoop) {
                return (LCAbstractLoop) parent;
            } else {
                parent = parent.parentNode;
            }
        }
        return null;
    }

    public Scope getEnclosingScope(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCStatementWithScope lcStatementWithScope) {
                return lcStatementWithScope.scope;
            } else if (parent instanceof LCExpressionWithScope lcExpressionWithScope) {
                return lcExpressionWithScope.scope;
            }
            parent = parent.parentNode;
        }
        System.err.println("getEnclosingScope()中，parent不应该是null");
        return null;
    }

    public LCSourceCodeFile getEnclosingSourceCodeFile(LCAstNode node) {
        LCAstNode parent = node.parentNode;
        while (parent != null) {
            if (parent instanceof LCSourceCodeFile lcSourceCodeFile) {
                return lcSourceCodeFile;
            }
            parent = parent.parentNode;
        }
        System.err.println("getEnclosingSourceCodeFile()中，parent不应该是null");
        return null;
    }

    public Object visitAst(LCAst ast, Object additional) {
        for (LCSourceFile lcSourceFile : ast.sourceFiles) {
            this.visit(lcSourceFile, additional);
        }
        return null;
    }

    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        for (LCAnnotation lcAnnotation : lcClassDeclaration.annotations) {
            this.visit(lcAnnotation, additional);
        }
        for (LCTypeParameter lcTypeParameter : lcClassDeclaration.typeParameters) {
            this.visitTypeParameter(lcTypeParameter, additional);
        }
        if (lcClassDeclaration.extended != null) {
            this.visitTypeReferenceExpression(lcClassDeclaration.extended, additional);
        }
        for (LCTypeReferenceExpression implementedInterface : lcClassDeclaration.implementedInterfaces) {
            this.visitTypeReferenceExpression(implementedInterface, additional);
        }
        for (LCTypeReferenceExpression permittedClass : lcClassDeclaration.permittedClasses) {
            this.visitTypeReferenceExpression(permittedClass, additional);
        }
        if (lcClassDeclaration.delegated != null) {
            this.visit(lcClassDeclaration.delegated, additional);
        }
        this.visitBlock(lcClassDeclaration.body, additional);
        return null;
    }

    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        for (LCAnnotation lcAnnotation : lcInterfaceDeclaration.annotations) {
            this.visit(lcAnnotation, additional);
        }
        for (LCTypeParameter lcTypeParameter : lcInterfaceDeclaration.typeParameters) {
            this.visitTypeParameter(lcTypeParameter, additional);
        }
        for (LCTypeReferenceExpression extendedInterface : lcInterfaceDeclaration.extendedInterfaces) {
            this.visitTypeReferenceExpression(extendedInterface, additional);
        }
        this.visitBlock(lcInterfaceDeclaration.body, additional);
        return null;
    }

    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        for (LCAnnotation lcAnnotation : lcRecordDeclaration.annotations) {
            this.visitAnnotation(lcAnnotation, additional);
        }
        for (LCTypeParameter lcTypeParameter : lcRecordDeclaration.typeParameters) {
            this.visitTypeParameter(lcTypeParameter, additional);
        }
        for (LCVariableDeclaration field : lcRecordDeclaration.fields) {
            this.visitVariableDeclaration(field, additional);
        }
        for (LCTypeReferenceExpression lcTypeReferenceExpression : lcRecordDeclaration.implementedInterfaces) {
            this.visitTypeReferenceExpression(lcTypeReferenceExpression, additional);
        }
        if (lcRecordDeclaration.delegated != null) {
            this.visit(lcRecordDeclaration.delegated, additional);
        }
        this.visitBlock(lcRecordDeclaration.body, additional);
        return null;
    }

    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
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
        for (LCEnumDeclaration.LCEnumFieldDeclaration field : lcEnumDeclaration.fields) {
            this.visitEnumFieldDeclaration(field, additional);
        }
        this.visitBlock(lcEnumDeclaration.body, additional);
        return null;
    }

    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object additional) {
        for (LCExpression expression : lcEnumFieldDeclaration.arguments) {
            this.visit(expression, additional);
        }
        return null;
    }

    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        for (LCAnnotation lcAnnotation : lcAnnotationDeclaration.annotations) {
            this.visitAnnotation(lcAnnotation, additional);
        }
        for (LCTypeParameter lcTypeParameter : lcAnnotationDeclaration.typeParameters) {
            this.visitTypeParameter(lcTypeParameter, additional);
        }
        this.visitAnnotationBody(lcAnnotationDeclaration.annotationBody, additional);
        return null;
    }

    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object additional) {
        for (LCAnnotationDeclaration.LCAnnotationFieldDeclaration field : lcAnnotationBody.fields) {
            this.visitAnnotationFieldDeclaration(field, additional);
        }
        return null;
    }

    public Object visitAnnotationFieldDeclaration(LCAnnotationDeclaration.LCAnnotationFieldDeclaration lcAnnotationFieldDeclaration, Object additional) {
        this.visit(lcAnnotationFieldDeclaration.typeExpression, additional);
        this.visit(lcAnnotationFieldDeclaration.defaultValue, lcAnnotationFieldDeclaration);
        return null;
    }

    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (lcVariableDeclaration.modifier != null) {
            this.visitModifier(lcVariableDeclaration.modifier, additional);
        }
        if (lcVariableDeclaration.typeExpression != null) {
            this.visit(lcVariableDeclaration.typeExpression, additional);
        }
        if (lcVariableDeclaration.extended != null) {
            this.visit(lcVariableDeclaration.extended, additional);
        }
        if (lcVariableDeclaration.delegated != null) {
            this.visit(lcVariableDeclaration.delegated, additional);
        }
        if (lcVariableDeclaration.init != null) {
            this.visit(lcVariableDeclaration.init, additional);
        }
        if (lcVariableDeclaration.getter != null) {
            this.visitMethodDeclaration(lcVariableDeclaration.getter, additional);
        }
        if (lcVariableDeclaration.setter != null) {
            this.visitMethodDeclaration(lcVariableDeclaration.setter, additional);
        }
        return null;
    }

    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (lcMethodDeclaration.modifier != null) {
            this.visitModifier(lcMethodDeclaration.modifier, additional);
        }
        for (LCTypeParameter typeParameter : lcMethodDeclaration.typeParameters) {
            this.visitTypeParameter(typeParameter, additional);
        }
        this.visitParameterList(lcMethodDeclaration.parameterList, additional);
        if (lcMethodDeclaration.returnTypeExpression != null) {
            this.visit(lcMethodDeclaration.returnTypeExpression, additional);
        }
        for (LCTypeExpression typeExpression : lcMethodDeclaration.threwExceptions) {
            this.visit(typeExpression, additional);
        }
        if (lcMethodDeclaration.extended != null) {
            this.visit(lcMethodDeclaration.extended, additional);
        }
        if (lcMethodDeclaration.body != null) {
            this.visit(lcMethodDeclaration.body, additional);
        }
        return null;
    }

    public Object visitParameterList(LCParameterList lcParameterList, Object additional) {
        for (LCVariableDeclaration x : lcParameterList.parameters) {
            this.visit(x, additional);
        }
        return null;
    }

    public Object visitBlock(LCBlock lcBlock, Object additional) {
        for (LCStatement x : lcBlock.statements) {
            this.visit(x, additional);
        }
        while (!lcBlock.statementQueue.isEmpty()) {
            LCStatement statement = lcBlock.statementQueue.poll();
            lcBlock.statements.add(statement);
            this.visit(statement, additional);
        }
        return null;
    }

    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object additional) {
        this.visit(lcExpressionStatement.expression, additional);
        return null;
    }

    public Object visitReturn(LCReturn lcReturn, Object additional) {
        if (lcReturn.returnedValue != null) {
            this.visit(lcReturn.returnedValue, additional);
        }
        return null;
    }

    public Object visitImport(LCImport lcImport, Object additional) {
        return null;
    }

    public Object visitIf(LCIf lcIf, Object additional) {
        this.visit(lcIf.condition, additional);
        this.visit(lcIf.then, additional);
        if (lcIf._else != null) {
            this.visit(lcIf._else, additional);
        }
        return null;
    }

    public Object visitFor(LCFor lcFor, Object additional) {
        if (lcFor.init != null) {
            this.visit(lcFor.init, additional);
        }
        if (lcFor.condition != null) {
            this.visit(lcFor.condition, additional);
        }
        if (lcFor.increment != null) {
            this.visit(lcFor.increment, additional);
        }
        this.visit(lcFor.body, additional);
        return null;
    }

    public Object visitForeach(LCForeach lcForeach, Object additional) {
        if (lcForeach.init != null) {
            this.visit(lcForeach.init, additional);
        }
        if (lcForeach.source != null) {
            this.visit(lcForeach.source, additional);
        }
        this.visit(lcForeach.body, additional);
        return null;
    }

    public Object visitBinary(LCBinary lcBinary, Object additional) {
        this.visit(lcBinary.expression1, additional);
        this.visit(lcBinary.expression2, additional);
        return null;
    }

    public Object visitUnary(LCUnary lcUnary, Object additional) {
        this.visit(lcUnary.expression, additional);
        return null;
    }

    // Literal
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object additional) {
        return null;
    }

    public Object visitDecimalLiteral(LCDecimalLiteral lcDecimalLiteral, Object additional) {
        return null;
    }

    public Object visitStringLiteral(LCStringLiteral lcStringLiteral, Object additional) {
        return null;
    }

    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object additional) {
        return null;
    }

    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object additional) {
        return null;
    }

    public Object visitBooleanLiteral(LCBooleanLiteral lcBooleanLiteral, Object additional) {
        return null;
    }

    public Object visitCharLiteral(LCCharLiteral lcCharLiteral, Object additional) {
        return null;
    }

    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object additional) {
        this.visit(lcArrayAccess.base, additional);
        this.visit(lcArrayAccess.index, additional);
        return null;
    }

    public Object visitVariable(LCVariable lcVariable, Object additional) {
        return null;
    }

    public Object visitMethodCall(LCMethodCall lcMethodCall, Object additional) {
        if (lcMethodCall.expression != null) {
            this.visit(lcMethodCall.expression, additional);
        }
        if (lcMethodCall.typeArguments != null) {
            for (LCTypeExpression typeArgument : lcMethodCall.typeArguments) {
                this.visit(typeArgument, additional);
            }
        }
        for (LCExpression param : lcMethodCall.arguments) {
            this.visit(param, additional);
        }
        return null;
    }

    public Object visitPredefinedTypeExpression(LCPredefinedTypeExpression lcPredefinedTypeExpression, Object additional) {
        return null;
    }

    public Object visitArrayTypeExpression(LCArrayTypeExpression lcArrayTypeExpression, Object additional) {
        this.visit(lcArrayTypeExpression.base, additional);
        return null;
    }

    public Object visitParenthesizedTypeExpression(LCParenthesizedTypeExpression lcParenthesizedTypeExpression, Object additional) {
        this.visit(lcParenthesizedTypeExpression.base, additional);
        return null;
    }

    public Object visitTypeReferenceExpression(LCTypeReferenceExpression lcTypeReferenceExpression, Object additional) {
        if (lcTypeReferenceExpression.typeArgs != null) {
            for (LCTypeExpression typeArg : lcTypeReferenceExpression.typeArgs) {
                this.visit(typeArg, additional);
            }
        }
        return null;
    }

    public Object visitMethodPointerTypeExpression(LCMethodPointerTypeExpression lcMethodPointerTypeExpression, Object additional) {
        this.visit(lcMethodPointerTypeExpression.parameterList, additional);
        this.visit(lcMethodPointerTypeExpression.returnTypeExpression, additional);
        return null;
    }

    public Object visitPointerTypeExpression(LCPointerTypeExpression lcPointerTypeExpression, Object additional) {
        this.visit(lcPointerTypeExpression.base, additional);
        return null;
    }

    public Object visitReferenceTypeExpression(LCReferenceTypeExpression lcReferenceTypeExpression, Object additional) {
        this.visit(lcReferenceTypeExpression.base, additional);
        return null;
    }

    public Object visitErrorExpression(LCErrorExpression lcErrorExpression, Object additional) {
        if (lcErrorExpression.expression != null) {
            this.visit(lcErrorExpression.expression, additional);
        }
        return null;
    }

    public Object visitErrorStatement(LCErrorStatement lcErrorStatement, Object additional) {
        if (lcErrorStatement.statement != null) {
            return this.visit(lcErrorStatement.statement, additional);
        }
        return null;
    }

    public Object visitThis(LCThis lcThis, Object additional) {
        return null;
    }

    public Object visitSuper(LCSuper lcSuper, Object additional) {
        return null;
    }

    public Object visitNewObject(LCNewObject lcNewObject, Object additional) {
        if (lcNewObject.place != null) {
            this.visit(lcNewObject.place, additional);
        }
        this.visit(lcNewObject.typeExpression, additional);
        for (LCExpression argument : lcNewObject.arguments) {
            this.visit(argument, additional);
        }
        return null;
    }

    public Object visitNewArray(LCNewArray lcNewArray, Object additional) {
        if (lcNewArray.place != null) {
            this.visit(lcNewArray.place, additional);
        }
        this.visit(lcNewArray.typeExpression, additional);
        for (LCExpression dimension : lcNewArray.dimensions) {
            if (dimension != null) this.visit(dimension, additional);
        }
        if (lcNewArray.elements != null) {
            for (LCExpression element : lcNewArray.elements) {
                this.visit(element, additional);
            }
        }
        return null;
    }

    public Object visitModifier(LCModifier lcModifier, Object additional) {
        return null;
    }

    public Object visitEmptyStatement(LCEmptyStatement lcEmptyStatement, Object additional) {
        return null;
    }

    public Object visitEmptyExpression(LCEmptyExpression lcEmptyExpression, Object additional) {
        return null;
    }

    public Object visitClone(LCClone lcClone, Object additional) {
        this.visit(lcClone.expression, additional);
        return null;
    }

    public Object visitDelete(LCDelete lcDelete, Object additional) {
        this.visit(lcDelete.expression, additional);
        return null;
    }

    public Object visitFree(LCFree lcFree, Object additional) {
        this.visit(lcFree.expression, additional);
        return null;
    }

    public Object visitGetAddress(LCGetAddress lcGetAddress, Object additional) {
        this.visit(lcGetAddress.expression, additional);
        return null;
    }

    public Object visitInstanceof(LCInstanceof lcInstanceof, Object additional) {
        this.visit(lcInstanceof.expression, additional);
        this.visit(lcInstanceof.typeExpression, additional);
        return null;
    }

    public Object visitMalloc(LCMalloc lcMalloc, Object additional) {
        this.visit(lcMalloc.size, additional);
        return null;
    }

    public Object visitSizeof(LCSizeof lcSizeof, Object additional) {
        this.visit(lcSizeof.expression, additional);
        return null;
    }

    public Object visitTypeCast(LCTypeCast lcTypeCast, Object additional) {
        this.visit(lcTypeCast.typeExpression, additional);
        this.visit(lcTypeCast.expression, additional);
        return null;
    }

    public Object visitTypeof(LCTypeof lcTypeof, Object additional) {
        this.visit(lcTypeof.expression, additional);
        return null;
    }

    public Object visitGoto(LCGoto lcGoto, Object additional) {
        return null;
    }

    public Object visitBreak(LCBreak lcBreak, Object additional) {
        return null;
    }

    public Object visitContinue(LCContinue lcContinue, Object additional) {
        return null;
    }

    public Object visitLoop(LCLoop lcLoop, Object additional) {
        this.visit(lcLoop.body, additional);
        return null;
    }

    public Object visitWhile(LCWhile lcWhile, Object additional) {
        this.visit(lcWhile.condition, additional);
        this.visit(lcWhile.body, additional);
        return null;
    }

    public Object visitDoWhile(LCDoWhile lcDoWhile, Object additional) {
        this.visit(lcDoWhile.condition, additional);
        this.visit(lcDoWhile.body, additional);
        return null;
    }

    public Object visitAnnotation(LCAnnotation lcAnnotation, Object additional) {
        for (LCAnnotation.LCAnnotationField argument : lcAnnotation.arguments) {
            this.visitAnnotationField(argument, additional);
        }
        return null;
    }

    public Object visitAnnotationField(LCAnnotation.LCAnnotationField lcAnnotationField, Object additional) {
        this.visit(lcAnnotationField.value, additional);
        return null;
    }

    public Object visitTernary(LCTernary lcTernary, Object additional) {
        this.visit(lcTernary.condition, additional);
        this.visit(lcTernary.then, additional);
        this.visit(lcTernary._else, additional);
        return null;
    }

    public Object visitDereference(LCDereference lcDereference, Object additional) {
        this.visit(lcDereference.expression, additional);
        return null;
    }

    public Object visitClassof(LCClassof lcClassof, Object additional) {
        this.visitTypeReferenceExpression(lcClassof.typeExpression, additional);
        return null;
    }

    public Object visitSynchronized(LCSynchronized lcSynchronized, Object additional) {
        this.visit(lcSynchronized.lock, additional);
        this.visit(lcSynchronized.body, additional);
        return null;
    }

    public Object visitThrow(LCThrow lcThrow, Object additional) {
        this.visit(lcThrow.expression, additional);
        return null;
    }

    public Object visitTry(LCTry lcTry, Object additional) {
        for (LCStatement resource : lcTry.resources) {
            this.visit(resource, additional);
        }
        this.visit(lcTry.base, additional);
        for (LCTry.LCCatch catcher : lcTry.catchers) {
            this.visitCatch(catcher, additional);
        }
        if (lcTry._finally != null) {
            this.visit(lcTry._finally, additional);
        }
        return null;
    }

    public Object visitCatch(LCTry.LCCatch lcCatch, Object additional) {
        this.visitVariableDeclaration(lcCatch.exceptionVariableDeclaration, additional);
        this.visit(lcCatch.then, additional);
        return null;
    }

    public Object visitAssert(LCAssert lcAssert, Object additional) {
        if (lcAssert.message != null) {
            this.visit(lcAssert.message, additional);
        }
        this.visit(lcAssert.condition, additional);
        return null;
    }

    public Object visitSourceFileProxy(LCSourceFileProxy lcSourceFileProxy, Object additional) {
        this.visit(lcSourceFileProxy.sourceFile, additional);
        return null;
    }

    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        this.visitBlock(lcSourceCodeFile.body, additional);
        return null;
    }

    public Object visitPackage(LCPackage lcPackage, Object additional) {
        return null;
    }

    public Object visitTypedef(LCTypedef lcTypedef, Object additional) {
        this.visit(lcTypedef.typeExpression, additional);
        return null;
    }

    public Object visitNative(LCNative lcNative, Object additional) {
        for (LCNative.LCResourceForNative resource : lcNative.resources) {
            this.visitResourceForNative(resource, additional);
        }
        for (LCNativeSection lcNativeSection : lcNative.sections) {
            this.visit(lcNativeSection, additional);
        }
        return null;
    }

    public Object visitNativeCode(LCNativeSection.LCNativeCode lcNativeCode, Object additional) {
        return null;
    }

    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        for (LCCase lcCase : lcSwitchExpression.cases) {
            this.visitCase(lcCase, additional);
        }
        this.visit(lcSwitchExpression.selector, additional);
        return null;
    }

    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object additional) {
        for (LCCase lcCase : lcSwitchStatement.cases) {
            this.visitCase(lcCase, additional);
        }
        this.visit(lcSwitchStatement.selector, additional);
        return null;
    }

    public Object visitCase(LCCase lcCase, Object additional) {
        for (LCCaseLabel label : lcCase.labels) {
            this.visit(label, additional);
        }
        if (lcCase.guard != null)
            this.visit(lcCase.guard, additional);
        for (LCStatement statement : lcCase.statements) {
            this.visit(statement, additional);
        }
        return null;
    }

    public Object visitDefaultCaseLabel(LCCaseLabel.LCDefaultCaseLabel lcDefaultCaseLabel, Object additional) {
        return null;
    }

    public Object visitConstantCaseLabel(LCCaseLabel.LCConstantCaseLabel lcConstantCaseLabel, Object additional) {
        this.visit(lcConstantCaseLabel.expression, additional);
        return null;
    }

    public Object visitTypeParameter(LCTypeParameter lcTypeParameter, Object additional) {
        if (lcTypeParameter.extended != null) {
            this.visitTypeReferenceExpression(lcTypeParameter.extended, additional);
        }
        for (LCTypeReferenceExpression type : lcTypeParameter.implemented) {
            this.visitTypeReferenceExpression(type, additional);
        }
        if (lcTypeParameter.supered != null) {
            this.visitTypeReferenceExpression(lcTypeParameter.supered, additional);
        }
        if (lcTypeParameter._default != null) {
            this.visitTypeReferenceExpression(lcTypeParameter._default, additional);
        }
        return null;
    }

    public Object visitIn(LCIn lcIn, Object additional) {
        this.visit(lcIn.expression1, additional);
        this.visit(lcIn.expression2, additional);
        return null;
    }

    public Object visitInclude(LCInclude lcInclude, Object additional) {
        return null;
    }

    public Object visitLambda(LCLambda lcLambda, Object additional) {
        for (LCTypeParameter typeParameter : lcLambda.typeParameters) {
            this.visitTypeParameter(typeParameter, additional);
        }
        this.visitParameterList(lcLambda.parameterList, additional);
        if (lcLambda.returnTypeExpression != null) {
            this.visit(lcLambda.returnTypeExpression, additional);
        }
        for (LCTypeReferenceExpression typeReferenceExpression : lcLambda.threwExceptions) {
            this.visitTypeReferenceExpression(typeReferenceExpression, additional);
        }
        this.visit(lcLambda.body, additional);
        return null;
    }

    public Object visitResourceForNative(LCNative.LCResourceForNative lcResourceForNative, Object additional) {
        this.visit(lcResourceForNative.resource, additional);
        return null;
    }

    public Object visitReferenceNativeFile(LCNativeSection.LCReferenceNativeFile lcReferenceNativeFile, Object additional) {
        if (lcReferenceNativeFile.beginLine != null) {
            this.visitIntegerLiteral(lcReferenceNativeFile.beginLine, additional);
        }
        if (lcReferenceNativeFile.endLine != null) {
            this.visitIntegerLiteral(lcReferenceNativeFile.endLine, additional);
        }
        return null;
    }

    public Object visitNullableTypeExpression(LCNullableTypeExpression lcNullableTypeExpression, Object additional) {
        this.visit(lcNullableTypeExpression.base, additional);
        return null;
    }

    public Object visitIs(LCIs lcIs, Object additional) {
        this.visit(lcIs.expression1, additional);
        this.visit(lcIs.expression2, additional);
        return null;
    }

    public Object visitPlatform(LCPlatform lcPlatform, Object additional) {
        return null;
    }

    public Object visitField(LCField lcField, Object additional) {
        return null;
    }

    public Object visitInit(LCInit lcInit, Object additional) {
        for (LCAnnotation annotation : lcInit.annotations) {
            this.visitAnnotation(annotation, additional);
        }
        this.visitBlock(lcInit.body, additional);
        return null;
    }

    public Object visitTypeCaseLabel(LCCaseLabel.LCTypeCaseLabel lcTypeCaseLabel, Object additional) {
        this.visit(lcTypeCaseLabel.typeExpression, additional);
        return null;
    }

    public Object visitRealloc(LCRealloc lcRealloc, Object additional) {
        this.visit(lcRealloc.expression, additional);
        this.visit(lcRealloc.size, additional);
        return null;
    }

    public Object visitAutoTypeExpression(LCAutoTypeExpression lcAutoTypeExpression, Object additional) {
        return null;
    }

    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object additional) {
        this.visit(lcNotNullAssert.base, additional);
        return null;
    }

    public Object visitYield(LCYield lcYield, Object additional) {
        this.visit(lcYield.value, additional);
        return null;
    }

    public Object visitWith(LCWith lcWith, Object additional) {
        for (LCVariableDeclaration resource : lcWith.resources) {
            this.visitVariableDeclaration(resource, additional);
        }
        this.visit(lcWith.body, additional);
        return null;
    }
}
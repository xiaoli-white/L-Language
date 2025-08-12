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
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.loops.*;

public final class LCAstDumper extends LCAstVisitor {
    @Override
    public Object visitAst(LCAst lcAst, Object prefix) {
        System.out.println((prefix instanceof String string && !string.isEmpty() ? prefix + "-" : "")
                + "LCAst" + (lcAst.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tmainMethod: " + (lcAst.mainMethod != null ? lcAst.mainMethod.name : "none"));
        System.out.println(prefix + "\t\tsourceFiles:");
        for (LCSourceFile sourceFile : lcAst.sourceFiles) {
            this.visit(sourceFile, prefix + "\t\t\t");
        }
        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object prefix) {
        System.out.println(prefix + "LCVariableDeclaration" + (lcVariableDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tisVal: " + lcVariableDeclaration.isVal);
        System.out.println(prefix + "\tname: '" + lcVariableDeclaration.name + "'");
        System.out.println(prefix + "\tmodifier:");
        this.visitModifier(lcVariableDeclaration.modifier, prefix + "\t\t");
        if (lcVariableDeclaration.typeExpression != null) {
            System.out.println(prefix + "\ttypeExpression:");
            this.visit(lcVariableDeclaration.typeExpression, prefix + "\t\t");
        }
        if (lcVariableDeclaration.extended != null) {
            System.out.println(prefix + "\textended:");
            this.visitTypeReferenceExpression(lcVariableDeclaration.extended, prefix + "\t\t");
        }
        if (lcVariableDeclaration.delegated != null) {
            System.out.println(prefix + "\tdelegated:");
            this.visit(lcVariableDeclaration.delegated, prefix + "\t\t");
        }
        if (lcVariableDeclaration.init != null) {
            System.out.println(prefix + "\tinit:");
            this.visit(lcVariableDeclaration.init, prefix + "\t\t");
        }
        if (lcVariableDeclaration.getter != null) {
            System.out.println(prefix + "\tgetter:");
            this.visitMethodDeclaration(lcVariableDeclaration.getter, prefix + "\t\t");
        }
        if (lcVariableDeclaration.setter != null) {
            System.out.println(prefix + "\tsetter:");
            this.visitMethodDeclaration(lcVariableDeclaration.setter, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object prefix) {
        System.out.println(prefix + "LCMethodDeclaration" + (lcMethodDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tmethodKind: " + lcMethodDeclaration.methodKind);
        if (!lcMethodDeclaration.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter typeParameter : lcMethodDeclaration.typeParameters) {
                this.visitTypeParameter(typeParameter, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tmodifier:");
        this.visitModifier(lcMethodDeclaration.modifier, prefix + "\t\t");
        System.out.println(prefix + "\tname: '" + lcMethodDeclaration.name + "'");
        System.out.println(prefix + "\tcallSignature:");
        this.visitParameterList(lcMethodDeclaration.parameterList, prefix + "\t\t");
        if (lcMethodDeclaration.returnTypeExpression != null) {
            System.out.println(prefix + "\treturnTypeExpression:");
            this.visit(lcMethodDeclaration.returnTypeExpression, prefix + "\t\t");
        }
        if (!lcMethodDeclaration.threwExceptions.isEmpty()) {
            System.out.println(prefix + "\tthrewExceptions:");
            for (LCTypeReferenceExpression threwException : lcMethodDeclaration.threwExceptions) {
                this.visitTypeReferenceExpression(threwException, prefix + "\t\t");
            }
        }
        if (lcMethodDeclaration.extended != null) {
            System.out.println(prefix + "\textended:");
            this.visitTypeReferenceExpression(lcMethodDeclaration.extended, prefix + "\t\t");
        }
        if (lcMethodDeclaration.body != null) {
            System.out.println(prefix + "\tbody:");
            this.visitBlock(lcMethodDeclaration.body, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitParameterList(LCParameterList lcParameterList, Object prefix) {
        System.out.println(prefix + "LCParameterList" + (lcParameterList.isErrorNode ? " **E**" : ""));
        if (!lcParameterList.parameters.isEmpty()) {
            System.out.println(prefix + "\tparameters:");
            for (LCVariableDeclaration parameter : lcParameterList.parameters) {
                this.visitVariableDeclaration(parameter, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object prefix) {
        System.out.println(prefix + "LCBlock" + (lcBlock.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcBlock.theType != null ? lcBlock.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcBlock.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcBlock.isLeftValue);
        if (!lcBlock.statements.isEmpty()) {
            System.out.println(prefix + "\tstatements:");
            for (LCStatement statement : lcBlock.statements) {
                this.visit(statement, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object prefix) {
        System.out.println(prefix + "LCExpressionStatement" + (lcExpressionStatement.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcExpressionStatement.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitReturn(LCReturn lcReturn, Object prefix) {
        System.out.println(prefix + "LCReturn" + (lcReturn.isErrorNode ? " **E**" : ""));
        if (lcReturn.returnedValue != null) {
            System.out.println(prefix + "\treturnedValue:");
            this.visit(lcReturn.returnedValue, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitImport(LCImport lcImport, Object prefix) {
        System.out.println(prefix + "LCImport" + (lcImport.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tkind: " + lcImport.kind);
        System.out.println(prefix + "\tname: '" + lcImport.name + "'");
        if (lcImport.alias != null) {
            System.out.println(prefix + "\talias: '" + lcImport.alias + "'");
        }
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object prefix) {
        System.out.println(prefix + "LCIf" + (lcIf.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcIf.theType != null ? lcIf.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcIf.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcIf.isLeftValue);
        System.out.println(prefix + "\tcondition:");
        this.visit(lcIf.condition, prefix + "\t\t");
        System.out.println(prefix + "\tthen:");
        this.visit(lcIf.then, prefix + "\t\t");
        if (lcIf._else != null) {
            System.out.println(prefix + "\telse:");
            this.visit(lcIf._else, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object prefix) {
        System.out.println(prefix + "LCFor" + (lcFor.isErrorNode ? " **E**" : ""));
        if (lcFor.init != null) {
            System.out.println(prefix + "\tinit:");
            this.visit(lcFor.init, prefix + "\t\t");
        }
        if (lcFor.condition != null) {
            System.out.println(prefix + "\tcondition:");
            this.visit(lcFor.condition, prefix + "\t\t");
        }
        if (lcFor.increment != null) {
            System.out.println(prefix + "\tincrement:");
            this.visit(lcFor.increment, prefix + "\t\t");
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcFor.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object prefix) {
        System.out.println(prefix + "LCForeach" + (lcForeach.isErrorNode ? " **E**" : ""));
        if (lcForeach.init != null) {
            System.out.println(prefix + "\tinit:");
            this.visit(lcForeach.init, prefix + "\t\t");
        }
        if (lcForeach.source != null) {
            System.out.println(prefix + "\tsource:");
            this.visit(lcForeach.source, prefix + "\t\t");
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcForeach.body, prefix + "\t\t");

        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object prefix) {
        System.out.println(prefix + "LCBinary" + (lcBinary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\toperator: " + lcBinary._operator);
        System.out.println(prefix + "\ttheType: " + (lcBinary.theType != null ? lcBinary.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcBinary.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcBinary.isLeftValue);
        System.out.println(prefix + "\texpression1:");
        this.visit(lcBinary.expression1, prefix + "\t\t");
        System.out.println(prefix + "\texpression2:");
        this.visit(lcBinary.expression2, prefix + "\t\t");

        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object prefix) {
        System.out.println(prefix + "LCUnary" + (lcUnary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\toperator: " + lcUnary._operator);
        System.out.println(prefix + "\tisPrefix: " + lcUnary.isPrefix);
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcUnary.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcUnary.isLeftValue);
        System.out.println(prefix + "\texpression:");
        this.visit(lcUnary.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object prefix) {
        System.out.println(prefix + "LCIntegerLiteral" + (lcIntegerLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcIntegerLiteral.theType != null ? lcIntegerLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: " + lcIntegerLiteral.value);
        return null;
    }

    @Override
    public Object visitDecimalLiteral(LCDecimalLiteral lcDecimalLiteral, Object prefix) {
        System.out.println(prefix + "LCDecimalLiteral" + (lcDecimalLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcDecimalLiteral.theType != null ? lcDecimalLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: " + lcDecimalLiteral.value);
        return null;
    }

    @Override
    public Object visitStringLiteral(LCStringLiteral lcStringLiteral, Object prefix) {
        System.out.println(prefix + "LCStringLiteral" + (lcStringLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcStringLiteral.theType != null ? lcStringLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: \"" + lcStringLiteral.value.replace("\n", "\\n") + "\"");
        return null;
    }

    @Override
    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object prefix) {
        System.out.println(prefix + "LCNullLiteral" + (lcNullLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcNullLiteral.theType != null ? lcNullLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: null");
        return null;
    }

    @Override
    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object prefix) {
        System.out.println(prefix + "LCNullptrLiteral" + (lcNullptrLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcNullptrLiteral.theType != null ? lcNullptrLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: nullptr");
        return null;
    }

    @Override
    public Object visitBooleanLiteral(LCBooleanLiteral lcBooleanLiteral, Object prefix) {
        System.out.println(prefix + "LCBooleanLiteral" + (lcBooleanLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcBooleanLiteral.theType != null ? lcBooleanLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: " + lcBooleanLiteral.value);
        return null;
    }

    @Override
    public Object visitCharLiteral(LCCharLiteral lcCharLiteral, Object prefix) {
        System.out.println(prefix + "LCCharLiteral" + (lcCharLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcCharLiteral.theType != null ? lcCharLiteral.theType : "<unknown>"));
        System.out.println(prefix + "\tvalue: " + lcCharLiteral.value);
        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object prefix) {
        System.out.println(prefix + "LCArrayAccess" + (lcArrayAccess.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcArrayAccess.theType != null ? lcArrayAccess.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcArrayAccess.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcArrayAccess.isLeftValue);
        System.out.println(prefix + "\tbase:");
        this.visit(lcArrayAccess.base, prefix + "\t\t");
        System.out.println(prefix + "\tindex:");
        this.visit(lcArrayAccess.index, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object prefix) {
        System.out.println(prefix + "LCNewObject" + (lcNewObject.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcNewObject.theType != null ? lcNewObject.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcNewObject.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcNewObject.isLeftValue);
        System.out.println(prefix + "\t<resolved>: " + (lcNewObject.constructorSymbol != null));
        if (lcNewObject.place != null) {
            System.out.println(prefix + "\tplace:");
            this.visit(lcNewObject.place, prefix + "\t\t");
        }
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcNewObject.typeExpression, prefix + "\t\t");
        System.out.println(prefix + "\targuments:");
        if (lcNewObject.arguments.isEmpty()) {
            System.out.println(prefix + "    \t<empty>");
        } else {
            for (LCExpression argument : lcNewObject.arguments) {
                this.visit(argument, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object prefix) {
        System.out.println(prefix + "LCNewArray" + (lcNewArray.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcNewArray.theType != null ? lcNewArray.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcNewArray.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcNewArray.isLeftValue);
        if (lcNewArray.place != null) {
            System.out.println(prefix + "\tplace");
            this.visit(lcNewArray.place, prefix + "\t\t");
        }
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcNewArray.typeExpression, prefix + "\t\t");
        System.out.println(prefix + "\tdimensions:");
        for (LCExpression dimension : lcNewArray.dimensions) {
            if (dimension == null)
                System.out.println(prefix + "    \t<null>");
            else
                this.visit(dimension, prefix + "\t\t");
        }
        if (lcNewArray.elements != null) {
            System.out.println(prefix + "\telements:");
            if (lcNewArray.elements.isEmpty()) {
                System.out.println(prefix + "    \t<empty>");
            } else {
                for (LCExpression element : lcNewArray.elements) {
                    this.visit(element, prefix + "\t\t");
                }
            }
        }
        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object prefix) {
        System.out.println(prefix + "LCVariable" + (lcVariable.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcVariable.name + "'");
        System.out.println(prefix + "\ttheType: " + (lcVariable.theType != null ? lcVariable.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcVariable.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcVariable.isLeftValue);
        System.out.println(prefix + "\t<resolved>: " + (lcVariable.symbol != null));
        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object prefix) {
        System.out.println(prefix + "LCMethodCall" + (lcMethodCall.isErrorNode ? " **E**" : ""));
        if (lcMethodCall.expression != null) {
            System.out.println(prefix + "\texpression:");
            this.visit(lcMethodCall.expression, prefix + "\t\t");
        } else {
            System.out.println(prefix + "\tname: '" + lcMethodCall.name + "'");
        }
        if (lcMethodCall.typeArguments != null) {
            System.out.println(prefix + "\ttypeArguments:");
            for (LCTypeExpression typeArgument : lcMethodCall.typeArguments) {
                this.visit(typeArgument, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\ttheType: " + (lcMethodCall.theType != null ? lcMethodCall.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcMethodCall.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcMethodCall.isLeftValue);
        if (lcMethodCall.expression == null) {
            System.out.println(prefix + "\t<resolved>: " + (lcMethodCall.symbol != null));
        }
        if (!lcMethodCall.arguments.isEmpty()) {
            System.out.println(prefix + "\targuments:");
            for (LCExpression argument : lcMethodCall.arguments) {
                this.visit(argument, prefix + "\t\t");
            }
        }

        return null;
    }

    @Override
    public Object visitPredefinedTypeExpression(LCPredefinedTypeExpression lcPredefinedTypeExpression, Object prefix) {
        System.out.println(prefix + "LCPredefinedTypeExpression" + (lcPredefinedTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tkeyword: " + lcPredefinedTypeExpression.keyword.getCode());
        System.out.println(prefix + "\ttheType: " + (lcPredefinedTypeExpression.theType != null ? lcPredefinedTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitArrayTypeExpression(LCArrayTypeExpression lcArrayTypeExpression, Object prefix) {
        System.out.println(prefix + "LCArrayTypeExpression: " + (lcArrayTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase: ");
        this.visit(lcArrayTypeExpression.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcArrayTypeExpression.theType != null ? lcArrayTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitParenthesizedTypeExpression(LCParenthesizedTypeExpression lcParenthesizedTypeExpression, Object prefix) {
        System.out.println(prefix + "LCParenthesizedType: " + (lcParenthesizedTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase: ");
        this.visit(lcParenthesizedTypeExpression.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcParenthesizedTypeExpression.theType != null ? lcParenthesizedTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitTypeReferenceExpression(LCTypeReferenceExpression lcTypeReferenceExpression, Object prefix) {
        System.out.println(prefix + "LCTypeReferenceExpression" + (lcTypeReferenceExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcTypeReferenceExpression.name + "'");
        if (lcTypeReferenceExpression.typeArgs != null && !lcTypeReferenceExpression.typeArgs.isEmpty()) {
            System.out.println(prefix + "\ttypeArgs:");
            for (LCTypeExpression typeExpression : lcTypeReferenceExpression.typeArgs) {
                this.visit(typeExpression, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\ttheType: " + (lcTypeReferenceExpression.theType != null ? lcTypeReferenceExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitMethodPointerTypeExpression(LCMethodPointerTypeExpression lcMethodPointerTypeExpression, Object prefix) {
        System.out.println(prefix + "LCMethodPointerTypeExpression" + (lcMethodPointerTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tparameterList:");
        this.visit(lcMethodPointerTypeExpression.parameterList, prefix + "\t\t");
        System.out.println(prefix + "\treturnType:");
        this.visit(lcMethodPointerTypeExpression.returnTypeExpression, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcMethodPointerTypeExpression.theType != null ? lcMethodPointerTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitPointerTypeExpression(LCPointerTypeExpression lcPointerTypeExpression, Object prefix) {
        System.out.println(prefix + "LCPointerTypeExpression:" + (lcPointerTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase: ");
        this.visit(lcPointerTypeExpression.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcPointerTypeExpression.theType != null ? lcPointerTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitReferenceTypeExpression(LCReferenceTypeExpression lcReferenceTypeExpression, Object prefix) {
        System.out.println(prefix + "LCReferenceTypeExpression" + (lcReferenceTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase: ");
        this.visit(lcReferenceTypeExpression.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcReferenceTypeExpression.theType != null ? lcReferenceTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitErrorExpression(LCErrorExpression lcErrorExpression, Object prefix) {
        System.out.println(prefix + "LCErrorExpression **E**");
        if (lcErrorExpression.expression != null) {
            System.out.println(prefix + "\texpression:");
            this.visit(lcErrorExpression.expression, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitErrorStatement(LCErrorStatement lcErrorStatement, Object prefix) {
        System.out.println(prefix + "LCErrorStatement **E**");
        if (lcErrorStatement.statement != null) {
            System.out.println(prefix + "\tstatement:");
            this.visit(lcErrorStatement.statement, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object prefix) {
        System.out.println(prefix + "LCThis");
        System.out.println(prefix + "\ttheType: " + (lcThis.theType != null ? lcThis.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcThis.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcThis.isLeftValue);
        System.out.println(prefix + "\t<resolved>: " + (lcThis.symbol != null));
        return null;
    }

    @Override
    public Object visitSuper(LCSuper lcSuper, Object prefix) {
        System.out.println(prefix + "LCSuper");
        System.out.println(prefix + "\ttheType: " + (lcSuper.theType != null ? lcSuper.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcSuper.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcSuper.isLeftValue);
        System.out.println(prefix + "\t<resolved>: " + (lcSuper.symbol != null));
        return null;
    }

    @Override
    public Object visitModifier(LCModifier lcModifier, Object prefix) {
        String e = (lcModifier.isErrorNode ? " **E**" : "");
        System.out.println(prefix + "modifier: " + "(0x" + Long.toHexString(lcModifier.flags) + ")" + LCFlags.toFlagsString(lcModifier.flags) + e);
        System.out.println(prefix + "attributes: " + lcModifier.attributes + e);
        System.out.println(prefix + "bitRange: " + lcModifier.bitRange + e);
        return null;
    }

    @Override
    public Object visitEmptyStatement(LCEmptyStatement lcEmptyStatement, Object prefix) {
        System.out.println(prefix + "LCEmptyStatement" + (lcEmptyStatement.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitEmptyExpression(LCEmptyExpression lcEmptyExpression, Object prefix) {
        System.out.println(prefix + "LCEmptyExpression" + (lcEmptyExpression.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitClone(LCClone lcClone, Object prefix) {
        System.out.println(prefix + "LCClone" + (lcClone.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcClone.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitDelete(LCDelete lcDelete, Object prefix) {
        System.out.println(prefix + "LCDelete" + (lcDelete.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcDelete.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitFree(LCFree lcFree, Object prefix) {
        System.out.println(prefix + "LCFree" + (lcFree.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcFree.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object prefix) {
        System.out.println(prefix + "LCGetReference" + (lcGetAddress.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcGetAddress.expression, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcGetAddress.theType != null ? lcGetAddress.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcGetAddress.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcGetAddress.isLeftValue);
        return null;
    }

    @Override
    public Object visitInstanceof(LCInstanceof lcInstanceof, Object prefix) {
        System.out.println(prefix + "LCInstanceof" + (lcInstanceof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcInstanceof.expression, prefix + "\t\t");
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcInstanceof.typeExpression, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcInstanceof.theType != null ? lcInstanceof.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcInstanceof.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcInstanceof.isLeftValue);
        return null;
    }

    @Override
    public Object visitMalloc(LCMalloc lcMalloc, Object prefix) {
        System.out.println(prefix + "LCMalloc" + (lcMalloc.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tsize:");
        this.visit(lcMalloc.size, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitSizeof(LCSizeof lcSizeof, Object prefix) {
        System.out.println(prefix + "LCSizeof" + (lcSizeof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcSizeof.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTypeCast(LCTypeCast lcTypeCast, Object prefix) {
        System.out.println(prefix + "LCTypeCast" + (lcTypeCast.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tkind: " + lcTypeCast.kind);
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcTypeCast.typeExpression, prefix + "\t\t");
        System.out.println(prefix + "\texpression:");
        this.visit(lcTypeCast.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTypeof(LCTypeof lcTypeof, Object prefix) {
        System.out.println(prefix + "LCTypeof" + (lcTypeof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcTypeof.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitGoto(LCGoto lcGoto, Object prefix) {
        System.out.println(prefix + "LCGoto" + (lcGoto.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tlabel: '" + lcGoto.label + "'");
        return null;
    }

    @Override
    public Object visitBreak(LCBreak lcBreak, Object prefix) {
        System.out.println(prefix + "LCBreak" + (lcBreak.isErrorNode ? " **E**" : ""));
        if (lcBreak.label != null) {
            System.out.println(prefix + "\tlabel: '" + lcBreak.label + "'");
        }
        return null;
    }

    @Override
    public Object visitContinue(LCContinue lcContinue, Object prefix) {
        System.out.println(prefix + "LCContinue" + (lcContinue.isErrorNode ? " **E**" : ""));
        if (lcContinue.label != null) {
            System.out.println(prefix + "\tlabel: " + lcContinue.label);
        }
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object prefix) {
        System.out.println(prefix + "LCLoop" + (lcLoop.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbody:");
        this.visit(lcLoop.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object prefix) {
        System.out.println(prefix + "LCWhile" + (lcWhile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tcondition:");
        this.visit(lcWhile.condition, prefix + "\t\t");
        System.out.println(prefix + "\tbody:");
        this.visit(lcWhile.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object prefix) {
        System.out.println(prefix + "LCDoWhile" + (lcDoWhile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbody:");
        this.visit(lcDoWhile.body, prefix + "\t\t");
        System.out.println(prefix + "\tcondition:");
        this.visit(lcDoWhile.condition, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object prefix) {
        System.out.println(prefix + "LCClassDeclaration" + (lcClassDeclaration.isErrorNode ? " **E**" : ""));
        if (!lcClassDeclaration.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcClassDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "\t\t");
            }
        }
        this.visitModifier(lcClassDeclaration.modifier, prefix + "\t");
        System.out.println(prefix + "\tname: '" + lcClassDeclaration.name + "'");
        if (!lcClassDeclaration.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter lcTypeParameter : lcClassDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "\t\t");
            }
        }
        if (lcClassDeclaration.extended != null) {
            System.out.println(prefix + "\textended:");
            this.visitTypeReferenceExpression(lcClassDeclaration.extended, prefix + "\t\t");
        }
        if (!lcClassDeclaration.implementedInterfaces.isEmpty()) {
            System.out.println(prefix + "\timplementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcClassDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "\t\t");
            }
        }
        if (!lcClassDeclaration.permittedClasses.isEmpty()) {
            System.out.println(prefix + "\tpermittedClasses:");
            for (LCTypeReferenceExpression permittedClass : lcClassDeclaration.permittedClasses) {
                this.visitTypeReferenceExpression(permittedClass, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcClassDeclaration.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object prefix) {
        System.out.println(prefix + "LCInterfaceDecl" + (lcInterfaceDeclaration.isErrorNode ? " **E**" : ""));
        if (!lcInterfaceDeclaration.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcInterfaceDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "\t\t");
            }
        }
        this.visitModifier(lcInterfaceDeclaration.modifier, prefix + "\t");
        System.out.println(prefix + "\tname: '" + lcInterfaceDeclaration.name + "'");
        if (!lcInterfaceDeclaration.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter lcTypeParameter : lcInterfaceDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "\t\t");
            }
        }
        if (!lcInterfaceDeclaration.extendedInterfaces.isEmpty()) {
            System.out.println(prefix + "\textendedInterfaces:");
            for (LCTypeReferenceExpression extendedInterface : lcInterfaceDeclaration.extendedInterfaces) {
                this.visitTypeReferenceExpression(extendedInterface, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcInterfaceDeclaration.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object prefix) {
        System.out.println(prefix + "LCAnnotationDecl" + (lcAnnotationDeclaration.isErrorNode ? " **E**" : ""));
        if (!lcAnnotationDeclaration.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcAnnotationDeclaration.annotations) {
                this.visit(lcAnnotation, prefix + "\t\t");
            }
        }
        this.visit(lcAnnotationDeclaration.modifier, prefix + "  ");
        System.out.println(prefix + "\tname: '" + lcAnnotationDeclaration.name + "'");
        System.out.println(prefix + "\tannotationBody:");
        this.visitAnnotationBody(lcAnnotationDeclaration.annotationBody, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object prefix) {
        System.out.println(prefix + "LCAnnotationBody" + (lcAnnotationBody.isErrorNode ? " **E**" : ""));
        if (!lcAnnotationBody.fields.isEmpty()) {
            System.out.println(prefix + "\tfields:");
            for (LCAnnotationDeclaration.LCAnnotationFieldDeclaration field : lcAnnotationBody.fields) {
                this.visit(field, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitAnnotationFieldDeclaration(LCAnnotationDeclaration.LCAnnotationFieldDeclaration lcAnnotationFieldDeclaration, Object prefix) {
        System.out.println(prefix + "LCAnnotationFieldDecl" + (lcAnnotationFieldDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcAnnotationFieldDeclaration.name + "'");
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcAnnotationFieldDeclaration.typeExpression, prefix + "\t\t");
        if (lcAnnotationFieldDeclaration.defaultValue != null) {
            System.out.println(prefix + "\tdefaultValue:");
            this.visit(lcAnnotationFieldDeclaration.defaultValue, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object prefix) {
        System.out.println(prefix + "LCEnumDeclaration" + (lcEnumDeclaration.isErrorNode ? " **E**" : ""));
        if (!lcEnumDeclaration.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcEnumDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "\t\t");
            }
        }
        this.visitModifier(lcEnumDeclaration.modifier, prefix + "\t");
        System.out.println(prefix + "\tname: '" + lcEnumDeclaration.name + "'");
        if (!lcEnumDeclaration.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter lcTypeParameter : lcEnumDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "\t\t");
            }
        }
        if (!lcEnumDeclaration.implementedInterfaces.isEmpty()) {
            System.out.println(prefix + "\timplementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcEnumDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tfields:");
        for (LCEnumDeclaration.LCEnumFieldDeclaration field : lcEnumDeclaration.fields) {
            this.visit(field, prefix + "\t\t");
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcEnumDeclaration.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object prefix) {
        System.out.println(prefix + "LCEnumFieldDecl" + (lcEnumFieldDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcEnumFieldDeclaration.name + "'");
        if (!lcEnumFieldDeclaration.arguments.isEmpty()) {
            System.out.println(prefix + "\targuments:");
            for (LCExpression argument : lcEnumFieldDeclaration.arguments) {
                this.visit(argument, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object prefix) {
        System.out.println(prefix + "LCRecordDecl" + (lcRecordDeclaration.isErrorNode ? " **E**" : ""));
        if (!lcRecordDeclaration.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcRecordDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "\t\t");
            }
        }
        this.visitModifier(lcRecordDeclaration.modifier, prefix + "\t");
        System.out.println(prefix + "\tname: '" + lcRecordDeclaration.name + "'");
        if (!lcRecordDeclaration.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter lcTypeParameter : lcRecordDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "\t\t");
            }
        }
        if (!lcRecordDeclaration.implementedInterfaces.isEmpty()) {
            System.out.println(prefix + "\timplementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcRecordDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "\t\t");
            }
        }

        System.out.println(prefix + "\tfields:");
        for (LCVariableDeclaration lcVariableDeclaration : lcRecordDeclaration.fields) {
            this.visit(lcVariableDeclaration, prefix + "\t\t");
        }

        System.out.println(prefix + "\tbody:");
        this.visit(lcRecordDeclaration.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitAnnotation(LCAnnotation lcAnnotation, Object prefix) {
        System.out.println(prefix + "LCAnnotation(name: '" + lcAnnotation.name + "')" + (lcAnnotation.isErrorNode ? " **E**" : ""));
        if (!lcAnnotation.arguments.isEmpty()) {
            System.out.println(prefix + "\targuments:");
            for (LCAnnotation.LCAnnotationField argument : lcAnnotation.arguments) {
                this.visitAnnotationField(argument, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitAnnotationField(LCAnnotation.LCAnnotationField lcAnnotationField, Object prefix) {
        System.out.println(prefix + "LCAnnotationField" + (lcAnnotationField.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcAnnotationField.name + "'");
        System.out.println(prefix + "\tvalue:");
        this.visit(lcAnnotationField.value, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTernary(LCTernary lcTernary, Object prefix) {
        System.out.println(prefix + "LCTernary" + (lcTernary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tcondition:");
        this.visit(lcTernary.condition, prefix + "\t\t");
        System.out.println(prefix + "\tthen:");
        this.visit(lcTernary.then, prefix + "\t\t");
        System.out.println(prefix + "\telse:");
        this.visit(lcTernary._else, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcTernary.theType != null ? lcTernary.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcTernary.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcTernary.isLeftValue);
        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object prefix) {
        System.out.println(prefix + "LCDereference" + (lcDereference.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcDereference.expression, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcDereference.theType != null ? lcDereference.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcDereference.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcDereference.isLeftValue);
        return null;
    }

    @Override
    public Object visitClassof(LCClassof lcClassof, Object prefix) {
        System.out.println(prefix + "LCClassof" + (lcClassof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcClassof.typeExpression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object prefix) {
        System.out.println(prefix + "LCSynchronized" + (lcSynchronized.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tlock:");
        this.visit(lcSynchronized.lock, prefix + "\t\t");
        System.out.println(prefix + "\tbody:");
        this.visit(lcSynchronized.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitThrow(LCThrow lcThrow, Object prefix) {
        System.out.println(prefix + "LCThrow" + (lcThrow.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcThrow.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object prefix) {
        System.out.println(prefix + "LCTry" + (lcTry.isErrorNode ? " **E**" : ""));
        if (!lcTry.resources.isEmpty()) {
            System.out.println(prefix + "\tresources:");
            for (LCStatement resource : lcTry.resources) {
                this.visit(resource, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tbase:");
        this.visit(lcTry.base, prefix + "\t\t");
        if (!lcTry.catchers.isEmpty()) {
            System.out.println(prefix + "\tcatchers:");
            for (LCTry.LCCatch catcher : lcTry.catchers) {
                this.visitCatch(catcher, prefix + "\t\t");
            }
        }
        if (lcTry._finally != null) {
            System.out.println(prefix + "\tfinally:");
            this.visit(lcTry._finally, prefix + "\t\t");
        }

        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object prefix) {
        System.out.println(prefix + "LCCatch" + (lcCatch.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texceptionVariableDecl:");
        this.visitVariableDeclaration(lcCatch.exceptionVariableDeclaration, prefix + "\t\t");
        System.out.println(prefix + "\tthen:");
        this.visit(lcCatch.then, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitAssert(LCAssert lcAssert, Object prefix) {
        System.out.println(prefix + "LCAssert" + (lcAssert.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tcondition:");
        this.visit(lcAssert.condition, prefix + "\t\t");
        if (lcAssert.message != null) {
            System.out.println(prefix + "\tmessage:");
            this.visit(lcAssert.message, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitSourceFileProxy(LCSourceFileProxy lcSourceFileProxy, Object prefix) {
        System.out.println(prefix + "LCSourceFileProxy" + (lcSourceFileProxy.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tsourceFile: '" + lcSourceFileProxy.sourceFile.filepath + "'");
        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object prefix) {
        System.out.println(prefix + "LCSourceCodeFile(filepath: '" + lcSourceCodeFile.filepath + "')" + (lcSourceCodeFile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tfilepath: '" + lcSourceCodeFile.filepath + "'");
        System.out.println(prefix + "\tbody:");
        this.visitBlock(lcSourceCodeFile.body, prefix + "\t\t");
        if (!lcSourceCodeFile.proxies.isEmpty()) {
            System.out.println(prefix + "\tproxies:");
            for (LCSourceFileProxy proxy : lcSourceCodeFile.proxies) {
                this.visitSourceFileProxy(proxy, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitPackage(LCPackage lcPackage, Object prefix) {
        System.out.println(prefix + "LCPackage" + (lcPackage.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcPackage.name + "'");
        return null;
    }

    @Override
    public Object visitTypedef(LCTypedef lcTypedef, Object prefix) {
        System.out.println(prefix + "LCTypedef" + (lcTypedef.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcTypedef.typeExpression, prefix + "\t\t");
        System.out.println(prefix + "\tname: '" + lcTypedef.name + "'");
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object prefix) {
        System.out.println(prefix + "LCNative" + (lcNative.isErrorNode ? " **E**" : ""));
        if (!lcNative.resources.isEmpty()) {
            System.out.println(prefix + "\tresources:");
            for (LCNative.LCResourceForNative resource : lcNative.resources) {
                this.visitResourceForNative(resource, prefix + "\t\t");
            }
        }
        if (!lcNative.sections.isEmpty()) {
            System.out.println(prefix + "\tsections:");
            for (LCNativeSection lcNativeSection : lcNative.sections) {
                this.visit(lcNativeSection, prefix + "\t\t");
            }
        }
        return null;
    }

    @Override
    public Object visitNativeCode(LCNativeSection.LCNativeCode lcNativeCode, Object prefix) {
        System.out.println(prefix + "LCNativeCode" + (lcNativeCode.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tcode: '" + lcNativeCode.code.replace("\n", "\\n") + "'");
        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object prefix) {
        System.out.println(prefix + "LCSwitchExpression" + (lcSwitchExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tselector:");
        this.visit(lcSwitchExpression.selector, prefix + "\t\t");
        System.out.println(prefix + "\tcases:");
        for (LCCase lcCase : lcSwitchExpression.cases) {
            this.visit(lcCase, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object prefix) {
        System.out.println(prefix + "LCSwitchStatement" + (lcSwitchStatement.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tselector:");
        this.visit(lcSwitchStatement.selector, prefix + "\t\t");
        System.out.println(prefix + "\tcases:");
        for (LCCase lcCase : lcSwitchStatement.cases) {
            this.visit(lcCase, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object prefix) {
        System.out.println(prefix + "LCCase" + (lcCase.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tlabels:");
        for (LCCaseLabel label : lcCase.labels) {
            this.visit(label, prefix + "\t\t");
        }
        if (lcCase.guard != null) {
            System.out.println(prefix + "\tguard:");
            this.visit(lcCase.guard, prefix + "\t\t");
        }
        System.out.println(prefix + "\tstatements:");
        for (LCStatement statement : lcCase.statements) {
            this.visit(statement, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitDefaultCaseLabel(LCCaseLabel.LCDefaultCaseLabel lcDefaultCaseLabel, Object prefix) {
        System.out.println(prefix + "LCDefaultCaseLabel" + (lcDefaultCaseLabel.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitConstantCaseLabel(LCCaseLabel.LCConstantCaseLabel lcConstantCaseLabel, Object prefix) {
        System.out.println(prefix + "LCConstantCaseLabel" + (lcConstantCaseLabel.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcConstantCaseLabel.expression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTypeParameter(LCTypeParameter lcTypeParameter, Object prefix) {
        System.out.println(prefix + "LCTypeParameter" + (lcTypeParameter.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcTypeParameter.name + "'");
        if (lcTypeParameter.extended != null) {
            System.out.println(prefix + "\textended:");
            this.visitTypeReferenceExpression(lcTypeParameter.extended, prefix + "\t\t");
        }
        if (!lcTypeParameter.implemented.isEmpty()) {
            System.out.println(prefix + "\timplemented:");
            for (LCTypeReferenceExpression typeReferenceExpression : lcTypeParameter.implemented) {
                this.visitTypeReferenceExpression(typeReferenceExpression, prefix + "\t\t");
            }
        }
        if (lcTypeParameter.supered != null) {
            System.out.println(prefix + "\tsupered:");
            this.visitTypeReferenceExpression(lcTypeParameter.supered, prefix + "\t\t");
        }
        if (lcTypeParameter._default != null) {
            System.out.println(prefix + "\tdefault:");
            this.visitTypeReferenceExpression(lcTypeParameter._default, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitIn(LCIn lcIn, Object prefix) {
        System.out.println(prefix + "LCIn" + (lcIn.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression1:");
        this.visit(lcIn.expression1, prefix + "\t\t");
        System.out.println(prefix + "\texpression2:");
        this.visit(lcIn.expression2, prefix + "\t");
        System.out.println(prefix + "\tisLeftValue: " + lcIn.isLeftValue);
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcIn.shouldBeLeftValue);
        System.out.println(prefix + "\tconstValue: " + lcIn.constValue);
        System.out.println(prefix + "\t<resolved>: " + (lcIn.symbol != null));
        return null;
    }

    @Override
    public Object visitInclude(LCInclude lcInclude, Object prefix) {
        System.out.println(prefix + "LCInclude" + (lcInclude.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tfilepath: '" + lcInclude.filepath + "'");
        if (lcInclude.beginLine != null) {
            System.out.println(prefix + "\tbeginLine:");
            this.visitIntegerLiteral(lcInclude.beginLine, prefix + "\t\t");
        }
        if (lcInclude.endLine != null) {
            System.out.println(prefix + "\tendLine:");
            this.visitIntegerLiteral(lcInclude.endLine, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object prefix) {
        System.out.println(prefix + "LCLambda" + (lcLambda.isErrorNode ? " **E**" : ""));
        if (!lcLambda.typeParameters.isEmpty()) {
            System.out.println(prefix + "\ttypeParameters:");
            for (LCTypeParameter typeParameter : lcLambda.typeParameters) {
                this.visitTypeParameter(typeParameter, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tmodifier:");
        this.visitModifier(lcLambda.modifier, prefix + "\t\t");
        System.out.println(prefix + "\tcallSignature:");
        this.visitParameterList(lcLambda.parameterList, prefix + "\t\t");
        if (lcLambda.returnTypeExpression != null) {
            System.out.println(prefix + "\treturnTypeExpression:");
            this.visit(lcLambda.returnTypeExpression, prefix + "\t\t");
        }

        if (!lcLambda.threwExceptions.isEmpty()) {
            System.out.println(prefix + "\tthrewExceptions:");
            for (LCTypeReferenceExpression threwException : lcLambda.threwExceptions) {
                this.visitTypeReferenceExpression(threwException, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcLambda.body, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcLambda.theType != null ? lcLambda.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcLambda.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcLambda.isLeftValue);
        return null;
    }

    @Override
    public Object visitResourceForNative(LCNative.LCResourceForNative lcResourceForNative, Object prefix) {
        System.out.println(prefix + "LCResourceForNative" + (lcResourceForNative.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tresource:");
        this.visit(lcResourceForNative.resource, prefix + "\t\t");
        System.out.println(prefix + "\tname: '" + lcResourceForNative.name + "'");
        return null;
    }

    @Override
    public Object visitReferenceNativeFile(LCNativeSection.LCReferenceNativeFile lcReferenceNativeFile, Object prefix) {
        System.out.println(prefix + "LCReferenceNativeFile" + (lcReferenceNativeFile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tname: '" + lcReferenceNativeFile.name + "'");
        if (lcReferenceNativeFile.beginLine != null) {
            System.out.println(prefix + "\tbeginLine:");
            this.visitIntegerLiteral(lcReferenceNativeFile.beginLine, prefix + "\t\t");
        }
        if (lcReferenceNativeFile.endLine != null) {
            System.out.println(prefix + "\tendLine:");
            this.visitIntegerLiteral(lcReferenceNativeFile.endLine, prefix + "\t\t");
        }
        return null;
    }

    @Override
    public Object visitNullableTypeExpression(LCNullableTypeExpression lcNullableTypeExpression, Object prefix) {
        System.out.println(prefix + "LCNullableTypeExpression" + (lcNullableTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase:");
        this.visit(lcNullableTypeExpression.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcNullableTypeExpression.theType != null ? lcNullableTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitIs(LCIs lcIs, Object prefix) {
        System.out.println(prefix + "LCIs" + (lcIs.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression1:");
        this.visit(lcIs.expression1, prefix + "\t\t");
        System.out.println(prefix + "\texpression2:");
        this.visit(lcIs.expression2, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcIs.theType != null ? lcIs.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcIs.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcIs.isLeftValue);
        return null;
    }

    @Override
    public Object visitPlatform(LCPlatform lcPlatform, Object prefix) {
        System.out.println(prefix + "LCPlatform" + (lcPlatform.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcPlatform.theType != null ? lcPlatform.theType : "<unknown>"));
        System.out.println(prefix + "\tconstValue: '" + lcPlatform.constValue + "'");
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcPlatform.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcPlatform.isLeftValue);
        return null;
    }

    @Override
    public Object visitField(LCField lcField, Object prefix) {
        System.out.println(prefix + "LCField" + (lcField.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcField.theType != null ? lcField.theType : "<unknown>"));
        System.out.println(prefix + "\tconstValue: '" + lcField.constValue + "'");
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcField.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcField.isLeftValue);
        return null;
    }

    @Override
    public Object visitInit(LCInit lcInit, Object prefix) {
        System.out.println(prefix + "LCInit" + (lcInit.isErrorNode ? " **E**" : ""));
        if (!lcInit.annotations.isEmpty()) {
            System.out.println(prefix + "\tannotations:");
            for (LCAnnotation lcAnnotation : lcInit.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tisStatic: " + lcInit.isStatic);
        System.out.println(prefix + "\tbody:");
        this.visitBlock(lcInit.body, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitTypeCaseLabel(LCCaseLabel.LCTypeCaseLabel lcTypeCaseLabel, Object prefix) {
        System.out.println(prefix + "LCTypeCaseLabel" + (lcTypeCaseLabel.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttypeExpression:");
        this.visit(lcTypeCaseLabel.typeExpression, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitRealloc(LCRealloc lcRealloc, Object prefix) {
        System.out.println(prefix + "LCRealloc" + (lcRealloc.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\texpression:");
        this.visit(lcRealloc.expression, prefix + "\t\t");
        System.out.println(prefix + "\tsize:");
        this.visit(lcRealloc.size, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitAutoTypeExpression(LCAutoTypeExpression lcAutoTypeExpression, Object prefix) {
        System.out.println(prefix + "LCAutoTypeExpression" + (lcAutoTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\ttheType: " + (lcAutoTypeExpression.theType != null ? lcAutoTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object prefix) {
        System.out.println(prefix + "LCNotNullAssert" + (lcNotNullAssert.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tbase:");
        this.visit(lcNotNullAssert.base, prefix + "\t\t");
        System.out.println(prefix + "\ttheType: " + (lcNotNullAssert.theType != null ? lcNotNullAssert.theType : "<unknown>"));
        System.out.println(prefix + "\tshouldBeLeftValue: " + lcNotNullAssert.shouldBeLeftValue);
        System.out.println(prefix + "\tisLeftValue: " + lcNotNullAssert.isLeftValue);
        return null;
    }

    @Override
    public Object visitYield(LCYield lcYield, Object prefix) {
        System.out.println(prefix + "LCYield" + (lcYield.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "\tvalue:");
        this.visit(lcYield.value, prefix + "\t\t");
        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object prefix) {
        System.out.println(prefix + "LCWith" + (lcWith.isErrorNode ? " **E**" : ""));
        if (!lcWith.resources.isEmpty()) {
            System.out.println(prefix + "\tresources:");
            for (LCStatement resource : lcWith.resources) {
                this.visit(resource, prefix + "\t\t");
            }
        }
        System.out.println(prefix + "\tbody:");
        this.visit(lcWith.body, prefix + "\t\t");

        return null;
    }
}
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

import java.util.Arrays;

public class LCAstDumper extends LCAstVisitor {
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
        System.out.println(prefix + "\thasSimiColon: " + lcVariableDeclaration.hasSemiColon);
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
        System.out.println(prefix + "-LCMethodDeclaration" + (lcMethodDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-methodKind: " + lcMethodDeclaration.methodKind);
        if (lcMethodDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter typeParameter : lcMethodDeclaration.typeParameters) {
                this.visitTypeParameter(typeParameter, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-modifier:");
        this.visitModifier(lcMethodDeclaration.modifier, prefix + "   |   |");
        System.out.println(prefix + "   |-name: '" + lcMethodDeclaration.name + "'");
        System.out.println(prefix + "   |-callSignature:");
        this.visitCallSignature(lcMethodDeclaration.callSignature, prefix + "   |   |");
        if (lcMethodDeclaration.returnTypeExpression != null) {
            System.out.println(prefix + "   |-returnTypeExpression:");
            this.visit(lcMethodDeclaration.returnTypeExpression, prefix + "   |   |");
        }
        if (lcMethodDeclaration.threwExceptions.length != 0) {
            System.out.println(prefix + "   |-threwExceptions:");
            for (LCTypeReferenceExpression threwException : lcMethodDeclaration.threwExceptions) {
                this.visitTypeReferenceExpression(threwException, prefix + "   |   |");
            }
        }
        if (lcMethodDeclaration.extended != null) {
            System.out.println(prefix + "   |-extended:");
            this.visitTypeReferenceExpression(lcMethodDeclaration.extended, prefix + "   |   |");
        }
        if (lcMethodDeclaration.body != null) {
            System.out.println(prefix + "   |-body:");
            this.visitBlock(lcMethodDeclaration.body, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitCallSignature(LCMethodDeclaration.LCCallSignature lcCallSignature, Object prefix) {
        System.out.println(prefix + "-LCCallSignature" + (lcCallSignature.isErrorNode ? " **E**" : ""));
        if (lcCallSignature.parameterList != null) {
            System.out.println(prefix + "   |-parameterList:");
            this.visitParameterList(lcCallSignature.parameterList, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitParameterList(LCParameterList lcParameterList, Object prefix) {
        System.out.println(prefix + "-LCParameterList" + (lcParameterList.isErrorNode ? " **E**" : ""));
        if (lcParameterList.parameters.length != 0) {
            System.out.println(prefix + "   |-parameters:");
            for (LCVariableDeclaration parameter : lcParameterList.parameters) {
                this.visitVariableDeclaration(parameter, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object prefix) {
        System.out.println(prefix + "-LCBlock" + (lcBlock.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcBlock.theType != null ? lcBlock.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcBlock.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcBlock.isLeftValue);
        if (lcBlock.statements.length != 0) {
            System.out.println(prefix + "   |-statements:");
            for (LCStatement statement : lcBlock.statements) {
                this.visit(statement, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object prefix) {
        System.out.println(prefix + "-LCExpressionStatement" + (lcExpressionStatement.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-hasSemiColon: " + lcExpressionStatement.hasSemiColon);
        System.out.println(prefix + "   |-expression:");
        this.visit(lcExpressionStatement.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitReturn(LCReturn lcReturn, Object prefix) {
        System.out.println(prefix + "-LCReturn" + (lcReturn.isErrorNode ? " **E**" : ""));
        if (lcReturn.returnedValue != null) {
            System.out.println(prefix + "   |-returnedValue:");
            this.visit(lcReturn.returnedValue, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitImport(LCImport lcImport, Object prefix) {
        System.out.println(prefix + "-LCImport" + (lcImport.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-kind: " + lcImport.kind);
        System.out.println(prefix + "   |-name: '" + lcImport.name + "'");
        if (lcImport.alias != null) {
            System.out.println(prefix + "   |-alias: '" + lcImport.alias + "'");
        }
        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object prefix) {
        System.out.println(prefix + "-LCIf" + (lcIf.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcIf.theType != null ? lcIf.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcIf.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcIf.isLeftValue);
        System.out.println(prefix + "   |-condition:");
        this.visit(lcIf.condition, prefix + "   |   |");
        System.out.println(prefix + "   |-then:");
        this.visit(lcIf.then, prefix + "   |   |");
        if (lcIf._else != null) {
            System.out.println(prefix + "   |-else:");
            this.visit(lcIf._else, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object prefix) {
        System.out.println(prefix + "-LCFor" + (lcFor.isErrorNode ? " **E**" : ""));
        if (lcFor.init != null) {
            System.out.println(prefix + "   |-init:");
            this.visit(lcFor.init, prefix + "   |   |");
        }
        if (lcFor.condition != null) {
            System.out.println(prefix + "   |-condition:");
            this.visit(lcFor.condition, prefix + "   |   |");
        }
        if (lcFor.increment != null) {
            System.out.println(prefix + "   |-increment:");
            this.visit(lcFor.increment, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcFor.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object prefix) {
        System.out.println(prefix + "-LCForeach" + (lcForeach.isErrorNode ? " **E**" : ""));
        if (lcForeach.init != null) {
            System.out.println(prefix + "   |-init:");
            this.visit(lcForeach.init, prefix + "   |   |");
        }
        if (lcForeach.source != null) {
            System.out.println(prefix + "   |-source:");
            this.visit(lcForeach.source, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcForeach.body, prefix + "       |");

        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object prefix) {
        System.out.println(prefix + "-LCBinary" + (lcBinary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-operator: " + lcBinary._operator);
        System.out.println(prefix + "   |-theType: " + (lcBinary.theType != null ? lcBinary.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcBinary.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcBinary.isLeftValue);
        System.out.println(prefix + "   |-expression1:");
        this.visit(lcBinary.expression1, prefix + "   |   |");
        System.out.println(prefix + "   |-expression2:");
        this.visit(lcBinary.expression2, prefix + "       |");

        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object prefix) {
        System.out.println(prefix + "-LCUnary" + (lcUnary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-operator: " + lcUnary._operator);
        System.out.println(prefix + "   |-isPrefix: " + lcUnary.isPrefix);
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcUnary.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcUnary.isLeftValue);
        System.out.println(prefix + "   |-expression:");
        this.visit(lcUnary.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitIntegerLiteral(LCIntegerLiteral lcIntegerLiteral, Object prefix) {
        System.out.println(prefix + "-LCIntegerLiteral" + (lcIntegerLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcIntegerLiteral.theType != null ? lcIntegerLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: " + lcIntegerLiteral.value);
        return null;
    }

    @Override
    public Object visitDecimalLiteral(LCDecimalLiteral lcDecimalLiteral, Object prefix) {
        System.out.println(prefix + "-LCDecimalLiteral" + (lcDecimalLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcDecimalLiteral.theType != null ? lcDecimalLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: " + lcDecimalLiteral.value);
        return null;
    }

    @Override
    public Object visitStringLiteral(LCStringLiteral lcStringLiteral, Object prefix) {
        System.out.println(prefix + "-LCStringLiteral" + (lcStringLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcStringLiteral.theType != null ? lcStringLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: \"" + lcStringLiteral.value.toString().replace("\n", "\\n") + "\"");
        return null;
    }

    @Override
    public Object visitNullLiteral(LCNullLiteral lcNullLiteral, Object prefix) {
        System.out.println(prefix + "-LCNullLiteral" + (lcNullLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcNullLiteral.theType != null ? lcNullLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: null");
        return null;
    }

    @Override
    public Object visitNullptrLiteral(LCNullptrLiteral lcNullptrLiteral, Object prefix) {
        System.out.println(prefix + "-LCNullptrLiteral" + (lcNullptrLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcNullptrLiteral.theType != null ? lcNullptrLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: nullptr");
        return null;
    }

    @Override
    public Object visitBooleanLiteral(LCBooleanLiteral lcBooleanLiteral, Object prefix) {
        System.out.println(prefix + "-LCBooleanLiteral" + (lcBooleanLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcBooleanLiteral.theType != null ? lcBooleanLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: " + lcBooleanLiteral.value);
        return null;
    }

    @Override
    public Object visitCharLiteral(LCCharLiteral lcCharLiteral, Object prefix) {
        System.out.println(prefix + "-LCCharLiteral" + (lcCharLiteral.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcCharLiteral.theType != null ? lcCharLiteral.theType : "<unknown>"));
        System.out.println(prefix + "   |-value: " + lcCharLiteral.value);
        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object prefix) {
        System.out.println(prefix + "-LCArrayAccess" + (lcArrayAccess.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcArrayAccess.theType != null ? lcArrayAccess.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcArrayAccess.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcArrayAccess.isLeftValue);
        System.out.println(prefix + "   |-base:");
        this.visit(lcArrayAccess.base, prefix + "   |   |");
        System.out.println(prefix + "   |-index:");
        this.visit(lcArrayAccess.index, prefix + "       |");
        return null;
    }

    @Override
    public Object visitNewObject(LCNewObject lcNewObject, Object prefix) {
        System.out.println(prefix + "-LCNewObject" + (lcNewObject.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcNewObject.theType != null ? lcNewObject.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcNewObject.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcNewObject.isLeftValue);
        if (lcNewObject.place != null) {
            System.out.println(prefix + "   |-place:");
            this.visit(lcNewObject.place, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcNewObject.typeExpression, prefix + "   |   |");
        System.out.println(prefix + "   |-arguments:");
        if (lcNewObject.arguments.length == 0) {
            System.out.println(prefix + "       |-<empty>");
        } else {
            for (LCExpression argument : lcNewObject.arguments) {
                this.visit(argument, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object prefix) {
        System.out.println(prefix + "-LCNewArray" + (lcNewArray.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcNewArray.theType != null ? lcNewArray.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcNewArray.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcNewArray.isLeftValue);
        if (lcNewArray.place != null) {
            System.out.println(prefix + "   |-place");
            this.visit(lcNewArray.place, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcNewArray.typeExpression, prefix + "   |   |");
        System.out.println(prefix + "   |-dimensions:");
        for (LCExpression dimension : lcNewArray.dimensions) {
            if (dimension == null)
                System.out.println(prefix + "       |-<null>");
            else
                this.visit(dimension, prefix + "   |   |");
        }
        if (lcNewArray.elements != null) {
            System.out.println(prefix + "   |-elements:");
            if (lcNewArray.elements.length == 0) {
                System.out.println(prefix + "       |-<empty>");
            } else {
                for (LCExpression element : lcNewArray.elements) {
                    this.visit(element, prefix + "       |");
                }
            }
        }
        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object prefix) {
        System.out.println(prefix + "-LCVariable" + (lcVariable.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcVariable.name + "'");
        System.out.println(prefix + "   |-theType: " + (lcVariable.theType != null ? lcVariable.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcVariable.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcVariable.isLeftValue);
        System.out.println(prefix + "   |-<resolved>: " + (lcVariable.symbol != null));
        return null;
    }

    @Override
    public Object visitMethodCall(LCMethodCall lcMethodCall, Object prefix) {
        System.out.println(prefix + "-LCMethodCall" + (lcMethodCall.isErrorNode ? " **E**" : ""));
        if (lcMethodCall.expression != null) {
            System.out.println(prefix + "   |-expression:");
            this.visit(lcMethodCall.expression, prefix + "   |   |");
        } else {
            System.out.println(prefix + "   |-name: '" + lcMethodCall.name + "'");
        }
        if (lcMethodCall.typeArguments != null) {
            System.out.println(prefix + "   |-typeArguments:");
            for (LCTypeExpression typeArgument : lcMethodCall.typeArguments) {
                this.visit(typeArgument, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-theType: " + (lcMethodCall.theType != null ? lcMethodCall.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcMethodCall.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcMethodCall.isLeftValue);
        if (lcMethodCall.expression == null) {
            System.out.println(prefix + "   |-<resolved>: " + (lcMethodCall.symbol != null));
        }
        if (lcMethodCall.arguments.length != 0) {
            System.out.println(prefix + "   |-arguments:");
            for (LCExpression argument : lcMethodCall.arguments) {
                this.visit(argument, prefix + "       |");
            }
        }

        return null;
    }

    @Override
    public Object visitPredefinedTypeExpression(LCPredefinedTypeExpression lcPredefinedTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCPredefinedTypeExpression" + (lcPredefinedTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-keyword: " + lcPredefinedTypeExpression.keyword.getCode());
        System.out.println(prefix + "   |-theType: " + (lcPredefinedTypeExpression.theType != null ? lcPredefinedTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitArrayTypeExpression(LCArrayTypeExpression lcArrayTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCArrayTypeExpression: " + (lcArrayTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base: ");
        this.visit(lcArrayTypeExpression.base, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcArrayTypeExpression.theType != null ? lcArrayTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitParenthesizedTypeExpression(LCParenthesizedTypeExpression lcParenthesizedTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCParenthesizedType: " + (lcParenthesizedTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base: ");
        this.visit(lcParenthesizedTypeExpression.base, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcParenthesizedTypeExpression.theType != null ? lcParenthesizedTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitTypeReferenceExpression(LCTypeReferenceExpression lcTypeReferenceExpression, Object prefix) {
        System.out.println(prefix + "-LCTypeReferenceExpression" + (lcTypeReferenceExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcTypeReferenceExpression.name + "'");
        if (lcTypeReferenceExpression.typeArgs != null && lcTypeReferenceExpression.typeArgs.length != 0) {
            System.out.println(prefix + "   |-typeArgs:");
            for (LCTypeExpression typeExpression : lcTypeReferenceExpression.typeArgs) {
                this.visit(typeExpression, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-theType: " + (lcTypeReferenceExpression.theType != null ? lcTypeReferenceExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitMethodPointerTypeExpression(LCMethodPointerTypeExpression lcMethodPointerTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCMethodPointerTypeExpression" + (lcMethodPointerTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-parameterList:");
        this.visit(lcMethodPointerTypeExpression.parameterList, prefix + "   |   |");
        System.out.println(prefix + "   |-returnType:");
        this.visit(lcMethodPointerTypeExpression.returnTypeExpression, prefix + "       |");
        System.out.println(prefix + "   |-theType: " + (lcMethodPointerTypeExpression.theType != null ? lcMethodPointerTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitPointerTypeExpression(LCPointerTypeExpression lcPointerTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCPointerTypeExpression:" + (lcPointerTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base: ");
        this.visit(lcPointerTypeExpression.base, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcPointerTypeExpression.theType != null ? lcPointerTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitReferenceTypeExpression(LCReferenceTypeExpression lcReferenceTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCReferenceTypeExpression" + (lcReferenceTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base: ");
        this.visit(lcReferenceTypeExpression.base, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcReferenceTypeExpression.theType != null ? lcReferenceTypeExpression.theType : "<unknown>"));
        return null;
    }

    @Override
    public Object visitErrorExpression(LCErrorExpression lcErrorExpression, Object prefix) {
        System.out.println(prefix + "-LCErrorExpression **E**");
        if (lcErrorExpression.expression != null) {
            System.out.println(prefix + "   |-expression:");
            this.visit(lcErrorExpression.expression, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitErrorStatement(LCErrorStatement lcErrorStatement, Object prefix) {
        System.out.println(prefix + "-LCErrorStatement **E**");
        if (lcErrorStatement.statement != null) {
            System.out.println(prefix + "   |-statement:");
            this.visit(lcErrorStatement.statement, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object prefix) {
        System.out.println(prefix + "-LCThis");
        System.out.println(prefix + "   |-theType: " + (lcThis.theType != null ? lcThis.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcThis.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcThis.isLeftValue);
        System.out.println(prefix + "   |-<resolved>: " + (lcThis.symbol != null));
        return null;
    }

    @Override
    public Object visitSuper(LCSuper lcSuper, Object prefix) {
        System.out.println(prefix + "-LCSuper");
        System.out.println(prefix + "   |-theType: " + (lcSuper.theType != null ? lcSuper.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcSuper.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcSuper.isLeftValue);
        System.out.println(prefix + "   |-<resolved>: " + (lcSuper.symbol != null));
        return null;
    }

    @Override
    public Object visitModifier(LCModifier lcModifier, Object prefix) {
        String e = (lcModifier.isErrorNode ? " **E**" : "");
        System.out.println(prefix + "-modifier: " + "(0x" + Long.toHexString(lcModifier.flags) + ")" + LCFlags.toFlagsString(lcModifier.flags) + e);
        System.out.println(prefix + "-attributes: " + Arrays.toString(lcModifier.attributes) + e);
        System.out.println(prefix + "-bitRange: " + lcModifier.bitRange + e);
        return null;
    }

    @Override
    public Object visitEmptyStatement(LCEmptyStatement lcEmptyStatement, Object prefix) {
        System.out.println(prefix + "-LCEmptyStatement" + (lcEmptyStatement.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitEmptyExpression(LCEmptyExpression lcEmptyExpression, Object prefix) {
        System.out.println(prefix + "-LCEmptyExpression" + (lcEmptyExpression.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitClone(LCClone lcClone, Object prefix) {
        System.out.println(prefix + "-LCClone" + (lcClone.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcClone.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitDelete(LCDelete lcDelete, Object prefix) {
        System.out.println(prefix + "-LCDelete" + (lcDelete.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcDelete.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitFree(LCFree lcFree, Object prefix) {
        System.out.println(prefix + "-LCFree" + (lcFree.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcFree.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object prefix) {
        System.out.println(prefix + "-LCGetReference" + (lcGetAddress.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcGetAddress.expression, prefix + "   |   |");

        if (lcGetAddress.name != null) {
            System.out.println(prefix + "   |-name: '" + lcGetAddress.name + "'");
        }

        if (lcGetAddress.paramTypeExpressions != null) {
            System.out.println(prefix + "   |-paramTypeExpressions:");
            if (lcGetAddress.paramTypeExpressions.length == 0) {
                System.out.println(prefix + "   |   |-<empty>");
            } else {
                for (LCTypeExpression paramTypeExpression : lcGetAddress.paramTypeExpressions) {
                    this.visit(paramTypeExpression, prefix + "   |   |");
                }
            }
        }
        System.out.println(prefix + "   |-theType: " + (lcGetAddress.theType != null ? lcGetAddress.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcGetAddress.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcGetAddress.isLeftValue);
        if (lcGetAddress.paramTypeExpressions != null) {
            System.out.println(prefix + "   |-<resolved>: " + (lcGetAddress.methodSymbol != null));
        }
        return null;
    }

    @Override
    public Object visitInstanceof(LCInstanceof lcInstanceof, Object prefix) {
        System.out.println(prefix + "-LCInstanceof" + (lcInstanceof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcInstanceof.expression, prefix + "   |   |");
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcInstanceof.typeExpression, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcInstanceof.theType != null ? lcInstanceof.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcInstanceof.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcInstanceof.isLeftValue);
        return null;
    }

    @Override
    public Object visitMalloc(LCMalloc lcMalloc, Object prefix) {
        System.out.println(prefix + "-LCMalloc" + (lcMalloc.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-size:");
        this.visit(lcMalloc.size, prefix + "       |");
        return null;
    }

    @Override
    public Object visitSizeof(LCSizeof lcSizeof, Object prefix) {
        System.out.println(prefix + "-LCSizeof" + (lcSizeof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcSizeof.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTypeCast(LCTypeCast lcTypeCast, Object prefix) {
        System.out.println(prefix + "-LCTypeCast" + (lcTypeCast.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-kind: " + lcTypeCast.kind);
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcTypeCast.typeExpression, prefix + "   |   |");
        System.out.println(prefix + "   |-expression:");
        this.visit(lcTypeCast.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTypeof(LCTypeof lcTypeof, Object prefix) {
        System.out.println(prefix + "-LCTypeof" + (lcTypeof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcTypeof.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitGoto(LCGoto lcGoto, Object prefix) {
        System.out.println(prefix + "-LCGoto" + (lcGoto.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-label: '" + lcGoto.label + "'");
        return null;
    }

    @Override
    public Object visitBreak(LCBreak lcBreak, Object prefix) {
        System.out.println(prefix + "-LCBreak" + (lcBreak.isErrorNode ? " **E**" : ""));
        if (lcBreak.label != null) {
            System.out.println(prefix + "   |-label: '" + lcBreak.label + "'");
        }
        return null;
    }

    @Override
    public Object visitContinue(LCContinue lcContinue, Object prefix) {
        System.out.println(prefix + "-LCContinue" + (lcContinue.isErrorNode ? " **E**" : ""));
        if (lcContinue.label != null) {
            System.out.println(prefix + "   |-label: " + lcContinue.label);
        }
        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object prefix) {
        System.out.println(prefix + "-LCLoop" + (lcLoop.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-body:");
        this.visit(lcLoop.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object prefix) {
        System.out.println(prefix + "-LCWhile" + (lcWhile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-condition:");
        this.visit(lcWhile.condition, prefix + "   |   |");
        System.out.println(prefix + "   |-body:");
        this.visit(lcWhile.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object prefix) {
        System.out.println(prefix + "-LCDoWhile" + (lcDoWhile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-body:");
        this.visit(lcDoWhile.body, prefix + "   |   |");
        System.out.println(prefix + "   |-condition:");
        this.visit(lcDoWhile.condition, prefix + "       |");
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object prefix) {
        System.out.println(prefix + "-LCClassDecl" + (lcClassDeclaration.isErrorNode ? " **E**" : ""));
        if (lcClassDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcClassDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visitModifier(lcClassDeclaration.modifier, prefix + "   |");
        System.out.println(prefix + "   |-name: '" + lcClassDeclaration.name + "'");
        if (lcClassDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter lcTypeParameter : lcClassDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "   |   |");
            }
        }
        if (lcClassDeclaration.extended != null) {
            System.out.println(prefix + "   |-extended:");
            this.visitTypeReferenceExpression(lcClassDeclaration.extended, prefix + "   |   |");
        }
        if (lcClassDeclaration.implementedInterfaces.length != 0) {
            System.out.println(prefix + "   |-implementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcClassDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "   |   |");
            }
        }
        if (lcClassDeclaration.permittedClasses.length != 0) {
            System.out.println(prefix + "   |-permittedClasses:");
            for (LCTypeReferenceExpression permittedClass : lcClassDeclaration.permittedClasses) {
                this.visitTypeReferenceExpression(permittedClass, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcClassDeclaration.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object prefix) {
        System.out.println(prefix + "-LCInterfaceDecl" + (lcInterfaceDeclaration.isErrorNode ? " **E**" : ""));
        if (lcInterfaceDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcInterfaceDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visitModifier(lcInterfaceDeclaration.modifier, prefix + "   |");
        System.out.println(prefix + "   |-name: '" + lcInterfaceDeclaration.name + "'");
        if (lcInterfaceDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter lcTypeParameter : lcInterfaceDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "   |   |");
            }
        }
        if (lcInterfaceDeclaration.extendedInterfaces.length != 0) {
            System.out.println(prefix + "   |-extendedInterfaces:");
            for (LCTypeReferenceExpression extendedInterface : lcInterfaceDeclaration.extendedInterfaces) {
                this.visitTypeReferenceExpression(extendedInterface, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcInterfaceDeclaration.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object prefix) {
        System.out.println(prefix + "LCAnnotationDecl" + (lcAnnotationDeclaration.isErrorNode ? " **E**" : ""));
        if (lcAnnotationDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcAnnotationDeclaration.annotations) {
                this.visit(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visit(lcAnnotationDeclaration.modifier, prefix + "  ");
        System.out.println(prefix + "   |-name: '" + lcAnnotationDeclaration.name + "'");
        System.out.println(prefix + "   |-annotationBody:");
        this.visitAnnotationBody(lcAnnotationDeclaration.annotationBody, prefix + "       |");
        return null;
    }

    @Override
    public Object visitAnnotationBody(LCAnnotationDeclaration.LCAnnotationBody lcAnnotationBody, Object prefix) {
        System.out.println(prefix + "-LCAnnotationBody" + (lcAnnotationBody.isErrorNode ? " **E**" : ""));
        if (lcAnnotationBody.fields.length != 0) {
            System.out.println(prefix + "   |-fields:");
            for (LCAnnotationDeclaration.LCAnnotationFieldDeclaration field : lcAnnotationBody.fields) {
                this.visit(field, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitAnnotationFieldDeclaration(LCAnnotationDeclaration.LCAnnotationFieldDeclaration lcAnnotationFieldDeclaration, Object prefix) {
        System.out.println(prefix + "-LCAnnotationFieldDecl" + (lcAnnotationFieldDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcAnnotationFieldDeclaration.name + "'");
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcAnnotationFieldDeclaration.typeExpression, prefix + "   |   |");
        if (lcAnnotationFieldDeclaration.defaultValue != null) {
            System.out.println(prefix + "   |-defaultValue:");
            this.visit(lcAnnotationFieldDeclaration.defaultValue, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object prefix) {
        System.out.println(prefix + "-LCEnumDecl" + (lcEnumDeclaration.isErrorNode ? " **E**" : ""));
        if (lcEnumDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcEnumDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visitModifier(lcEnumDeclaration.modifier, prefix + "   |");
        System.out.println(prefix + "   |-name: '" + lcEnumDeclaration.name + "'");
        if (lcEnumDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter lcTypeParameter : lcEnumDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "   |   |");
            }
        }
        if (lcEnumDeclaration.implementedInterfaces.length != 0) {
            System.out.println(prefix + "   |-implementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcEnumDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-fields:");
        for (LCEnumDeclaration.LCEnumFieldDeclaration field : lcEnumDeclaration.fields) {
            this.visit(field, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcEnumDeclaration.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitEnumFieldDeclaration(LCEnumDeclaration.LCEnumFieldDeclaration lcEnumFieldDeclaration, Object prefix) {
        System.out.println(prefix + "-LCEnumFieldDecl" + (lcEnumFieldDeclaration.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcEnumFieldDeclaration.name + "'");
        if (lcEnumFieldDeclaration.arguments.length != 0) {
            System.out.println(prefix + "   |-arguments:");
            for (LCExpression argument : lcEnumFieldDeclaration.arguments) {
                this.visit(argument, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object prefix) {
        System.out.println(prefix + "LCRecordDecl" + (lcRecordDeclaration.isErrorNode ? " **E**" : ""));
        if (lcRecordDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcRecordDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visitModifier(lcRecordDeclaration.modifier, prefix + "   |");
        System.out.println(prefix + "   |-name: '" + lcRecordDeclaration.name + "'");
        if (lcRecordDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter lcTypeParameter : lcRecordDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "   |   |");
            }
        }
        if (lcRecordDeclaration.implementedInterfaces.length != 0) {
            System.out.println(prefix + "   |-implementedInterfaces:");
            for (LCTypeReferenceExpression implementedInterface : lcRecordDeclaration.implementedInterfaces) {
                this.visitTypeReferenceExpression(implementedInterface, prefix + "   |   |");
            }
        }

        System.out.println(prefix + "   |-fields:");
        for (LCVariableDeclaration lcVariableDeclaration : lcRecordDeclaration.fields) {
            this.visit(lcVariableDeclaration, prefix + "   |   |");
        }

        System.out.println(prefix + "   |-body:");
        this.visit(lcRecordDeclaration.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitStructDeclaration(LCStructDeclaration lcStructDeclaration, Object prefix) {
        System.out.println(prefix + "-LCStructDecl" + (lcStructDeclaration.isErrorNode ? " **E**" : ""));
        if (lcStructDeclaration.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcStructDeclaration.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        this.visitModifier(lcStructDeclaration.modifier, prefix + "   |");
        System.out.println(prefix + "   |-name: '" + lcStructDeclaration.name + "'");
        if (lcStructDeclaration.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter lcTypeParameter : lcStructDeclaration.typeParameters) {
                this.visit(lcTypeParameter, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcStructDeclaration.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitAnnotation(LCAnnotationDeclaration.LCAnnotation lcAnnotation, Object prefix) {
        System.out.println(prefix + "-LCAnnotation(name: '" + lcAnnotation.name + "')" + (lcAnnotation.isErrorNode ? " **E**" : ""));
        if (lcAnnotation.arguments.length == 0) {
            System.out.println(prefix + "   |-arguments:");
            for (LCAnnotationDeclaration.LCAnnotation.LCAnnotationField argument : lcAnnotation.arguments) {
                this.visitAnnotationField(argument, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitAnnotationField(LCAnnotationDeclaration.LCAnnotation.LCAnnotationField lcAnnotationField, Object prefix) {
        System.out.println(prefix + "-LCAnnotationField" + (lcAnnotationField.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcAnnotationField.name + "'");
        System.out.println(prefix + "   |-value:");
        this.visit(lcAnnotationField.value, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTernary(LCTernary lcTernary, Object prefix) {
        System.out.println(prefix + "-LCTernary" + (lcTernary.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-condition:");
        this.visit(lcTernary.condition, prefix + "   |   |");
        System.out.println(prefix + "   |-then:");
        this.visit(lcTernary.then, prefix + "   |   |");
        System.out.println(prefix + "   |-else:");
        this.visit(lcTernary._else, prefix + "       |");
        System.out.println(prefix + "   |-theType: " + (lcTernary.theType != null ? lcTernary.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcTernary.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcTernary.isLeftValue);
        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object prefix) {
        System.out.println(prefix + "-LCDereference" + (lcDereference.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcDereference.expression, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcDereference.theType != null ? lcDereference.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcDereference.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcDereference.isLeftValue);
        return null;
    }

    @Override
    public Object visitClassof(LCClassof lcClassof, Object prefix) {
        System.out.println(prefix + "-LCClassof" + (lcClassof.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcClassof.typeExpression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object prefix) {
        System.out.println(prefix + "-LCSynchronized" + (lcSynchronized.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-lock:");
        this.visit(lcSynchronized.lock, prefix + "   |   |");
        System.out.println(prefix + "   |-body:");
        this.visit(lcSynchronized.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitThrow(LCThrow lcThrow, Object prefix) {
        System.out.println(prefix + "-LCThrow" + (lcThrow.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcThrow.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object prefix) {
        System.out.println(prefix + "-LCTry" + (lcTry.isErrorNode ? " **E**" : ""));
        if (lcTry.resources.length != 0) {
            System.out.println(prefix + "   |-resources:");
            for (LCStatement resource : lcTry.resources) {
                this.visit(resource, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-base:");
        this.visit(lcTry.base, prefix + "   |   |");
        if (lcTry.catchers.length != 0) {
            System.out.println(prefix + "   |-catchers:");
            for (LCTry.LCCatch catcher : lcTry.catchers) {
                this.visitCatch(catcher, prefix + "   |   |");
            }
        }
        if (lcTry._finally != null) {
            System.out.println(prefix + "   |-finally:");
            this.visit(lcTry._finally, prefix + "       |");
        }

        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object prefix) {
        System.out.println(prefix + "-LCCatch" + (lcCatch.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-exceptionVariableDecl:");
        this.visitVariableDeclaration(lcCatch.exceptionVariableDeclaration, prefix + "   |   |");
        System.out.println(prefix + "   |-then:");
        this.visit(lcCatch.then, prefix + "       |");
        return null;
    }

    @Override
    public Object visitAssert(LCAssert lcAssert, Object prefix) {
        System.out.println(prefix + "-LCAssert" + (lcAssert.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-condition:");
        this.visit(lcAssert.condition, prefix + "   |   |");
        if (lcAssert.message != null) {
            System.out.println(prefix + "   |-message:");
            this.visit(lcAssert.message, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitSourceFileProxy(LCSourceFileProxy lcSourceFileProxy, Object prefix) {
        System.out.println(prefix + "-LCSourceFileProxy" + (lcSourceFileProxy.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-sourceFile: '" + lcSourceFileProxy.sourceFile.filepath + "'");
        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object prefix) {
        System.out.println(prefix + "-LCSourceCodeFile(filepath: '" + lcSourceCodeFile.filepath + "')" + (lcSourceCodeFile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-filepath: '" + lcSourceCodeFile.filepath + "'");
        System.out.println(prefix + "   |-body:");
        this.visitBlock(lcSourceCodeFile.body, prefix + "   |   |");
        if (lcSourceCodeFile.proxies.length > 0) {
            System.out.println(prefix + "   |-proxies:");
            for (LCSourceFileProxy proxy : lcSourceCodeFile.proxies) {
                this.visitSourceFileProxy(proxy, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitPackage(LCPackage lcPackage, Object prefix) {
        System.out.println(prefix + "-LCPackage" + (lcPackage.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcPackage.name + "'");
        return null;
    }

    @Override
    public Object visitTypedef(LCTypedef lcTypedef, Object prefix) {
        System.out.println(prefix + "-LCTypedef" + (lcTypedef.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcTypedef.typeExpression, prefix + "   |   |");
        System.out.println(prefix + "   |-name: '" + lcTypedef.name + "'");
        return null;
    }

    @Override
    public Object visitNative(LCNative lcNative, Object prefix) {
        System.out.println(prefix + "-LCNative" + (lcNative.isErrorNode ? " **E**" : ""));
        if (lcNative.resources.length != 0) {
            System.out.println(prefix + "   |-resources:");
            for (LCNative.LCResourceForNative resource : lcNative.resources) {
                this.visitResourceForNative(resource, prefix + "   |   |");
            }
        }
        if (lcNative.sections.length != 0) {
            System.out.println(prefix + "   |-sections:");
            for (LCNativeSection lcNativeSection : lcNative.sections) {
                this.visit(lcNativeSection, prefix + "       |");
            }
        }
        return null;
    }

    @Override
    public Object visitNativeCode(LCNativeSection.LCNativeCode lcNativeCode, Object prefix) {
        System.out.println(prefix + "-LCNativeCode" + (lcNativeCode.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-code: '" + lcNativeCode.code.replace("\n", "\\n") + "'");
        return null;
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object prefix) {
        System.out.println(prefix + "-LCSwitchExpression" + (lcSwitchExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-selector:");
        this.visit(lcSwitchExpression.selector, prefix + "   |   |");
        System.out.println(prefix + "   |-cases:");
        for (LCCase lcCase : lcSwitchExpression.cases) {
            this.visit(lcCase, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitSwitchStatement(LCSwitchStatement lcSwitchStatement, Object prefix) {
        System.out.println(prefix + "-LCSwitchStatement" + (lcSwitchStatement.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-selector:");
        this.visit(lcSwitchStatement.selector, prefix + "   |   |");
        System.out.println(prefix + "   |-cases:");
        for (LCCase lcCase : lcSwitchStatement.cases) {
            this.visit(lcCase, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitCase(LCCase lcCase, Object prefix) {
        System.out.println(prefix + "-LCCase" + (lcCase.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-labels:");
        for (LCCaseLabel label : lcCase.labels) {
            this.visit(label, prefix + "   |   |");
        }
        if (lcCase.guard != null) {
            System.out.println(prefix + "   |-guard:");
            this.visit(lcCase.guard, prefix + "   |   |");
        }
        System.out.println(prefix + "   |-statements:");
        for (LCStatement statement : lcCase.statements) {
            this.visit(statement, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitDefaultCaseLabel(LCCaseLabel.LCDefaultCaseLabel lcDefaultCaseLabel, Object prefix) {
        System.out.println(prefix + "-LCDefaultCaseLabel" + (lcDefaultCaseLabel.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitConstantCaseLabel(LCCaseLabel.LCConstantCaseLabel lcConstantCaseLabel, Object prefix) {
        System.out.println(prefix + "-LCConstantCaseLabel" + (lcConstantCaseLabel.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcConstantCaseLabel.expression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTypeParameter(LCTypeParameter lcTypeParameter, Object prefix) {
        System.out.println(prefix + "-LCTypeParameter" + (lcTypeParameter.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcTypeParameter.name + "'");
        if (lcTypeParameter.extended != null) {
            System.out.println(prefix + "   |-extended:");
            this.visitTypeReferenceExpression(lcTypeParameter.extended, prefix + "   |   |");
        }
        if (lcTypeParameter.implemented.length != 0) {
            System.out.println(prefix + "   |-implemented:");
            for (LCTypeReferenceExpression typeReferenceExpression : lcTypeParameter.implemented) {
                this.visitTypeReferenceExpression(typeReferenceExpression, prefix + "   |   |");
            }
        }
        if (lcTypeParameter.supered != null) {
            System.out.println(prefix + "   |-supered:");
            this.visitTypeReferenceExpression(lcTypeParameter.supered, prefix + "   |   |");
        }
        if (lcTypeParameter._default != null) {
            System.out.println(prefix + "   |-default:");
            this.visitTypeReferenceExpression(lcTypeParameter._default, prefix + "       |");
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
        System.out.println(prefix + "-LCInclude" + (lcInclude.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-filepath: '" + lcInclude.filepath + "'");
        if (lcInclude.beginLine != null) {
            System.out.println(prefix + "   |-beginLine:");
            this.visitIntegerLiteral(lcInclude.beginLine, prefix + "   |   |");
        }
        if (lcInclude.endLine != null) {
            System.out.println(prefix + "   |-endLine:");
            this.visitIntegerLiteral(lcInclude.endLine, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object prefix) {
        System.out.println(prefix + "-LCLambda" + (lcLambda.isErrorNode ? " **E**" : ""));
        if (lcLambda.typeParameters.length != 0) {
            System.out.println(prefix + "   |-typeParameters:");
            for (LCTypeParameter typeParameter : lcLambda.typeParameters) {
                this.visitTypeParameter(typeParameter, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-modifier:");
        this.visitModifier(lcLambda.modifier, prefix + "   |   |");
        System.out.println(prefix + "   |-callSignature:");
        this.visitCallSignature(lcLambda.callSignature, prefix + "   |   |");
        if (lcLambda.returnTypeExpression != null) {
            System.out.println(prefix + "   |-returnTypeExpression:");
            this.visit(lcLambda.returnTypeExpression, prefix + "   |   |");
        }

        if (lcLambda.threwExceptions.length != 0) {
            System.out.println(prefix + "   |-threwExceptions:");
            for (LCTypeReferenceExpression threwException : lcLambda.threwExceptions) {
                this.visitTypeReferenceExpression(threwException, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcLambda.body, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcLambda.theType != null ? lcLambda.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcLambda.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcLambda.isLeftValue);
        return null;
    }

    @Override
    public Object visitResourceForNative(LCNative.LCResourceForNative lcResourceForNative, Object prefix) {
        System.out.println(prefix + "-LCResourceForNative" + (lcResourceForNative.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-resource:");
        this.visit(lcResourceForNative.resource, prefix + "   |   |");
        System.out.println(prefix + "   |-name: '" + lcResourceForNative.name + "'");
        return null;
    }

    @Override
    public Object visitReferenceNativeFile(LCNativeSection.LCReferenceNativeFile lcReferenceNativeFile, Object prefix) {
        System.out.println(prefix + "-LCReferenceNativeFile" + (lcReferenceNativeFile.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-name: '" + lcReferenceNativeFile.name + "'");
        if (lcReferenceNativeFile.beginLine != null) {
            System.out.println(prefix + "   |-beginLine:");
            this.visitIntegerLiteral(lcReferenceNativeFile.beginLine, prefix + "   |   |");
        }
        if (lcReferenceNativeFile.endLine != null) {
            System.out.println(prefix + "   |-endLine:");
            this.visitIntegerLiteral(lcReferenceNativeFile.endLine, prefix + "       |");
        }
        return null;
    }

    @Override
    public Object visitNullableTypeExpression(LCNullableTypeExpression lcNullableTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCNullableTypeExpression" + (lcNullableTypeExpression.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base:");
        this.visit(lcNullableTypeExpression.base, prefix + "       |");
        return null;
    }

    @Override
    public Object visitIs(LCIs lcIs, Object prefix) {
        System.out.println(prefix + "-LCIs" + (lcIs.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression1:");
        this.visit(lcIs.expression1, prefix + "   |   |");
        System.out.println(prefix + "   |-expression2:");
        this.visit(lcIs.expression2, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcIs.theType != null ? lcIs.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcIs.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcIs.isLeftValue);
        return null;
    }

    @Override
    public Object visitPlatform(LCPlatform lcPlatform, Object prefix) {
        System.out.println(prefix + "-LCPlatform" + (lcPlatform.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcPlatform.theType != null ? lcPlatform.theType : "<unknown>"));
        System.out.println(prefix + "   |-constValue: '" + lcPlatform.constValue + "'");
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcPlatform.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcPlatform.isLeftValue);
        return null;
    }

    @Override
    public Object visitField(LCField lcField, Object prefix) {
        System.out.println(prefix + "-LCField" + (lcField.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-theType: " + (lcField.theType != null ? lcField.theType : "<unknown>"));
        System.out.println(prefix + "   |-constValue: '" + lcField.constValue + "'");
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcField.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcField.isLeftValue);
        return null;
    }

    @Override
    public Object visitInit(LCInit lcInit, Object prefix) {
        System.out.println(prefix + "-LCInit" + (lcInit.isErrorNode ? " **E**" : ""));
        if (lcInit.annotations.length != 0) {
            System.out.println(prefix + "   |-annotations:");
            for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : lcInit.annotations) {
                this.visitAnnotation(lcAnnotation, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-isStatic: " + lcInit.isStatic);
        System.out.println(prefix + "   |-body:");
        this.visitBlock(lcInit.body, prefix + "       |");
        return null;
    }

    @Override
    public Object visitTypeCaseLabel(LCCaseLabel.LCTypeCaseLabel lcTypeCaseLabel, Object prefix) {
        System.out.println(prefix + "-LCTypeCaseLabel" + (lcTypeCaseLabel.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-typeExpression:");
        this.visit(lcTypeCaseLabel.typeExpression, prefix + "       |");
        return null;
    }

    @Override
    public Object visitRealloc(LCRealloc lcRealloc, Object prefix) {
        System.out.println(prefix + "-LCRealloc" + (lcRealloc.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-expression:");
        this.visit(lcRealloc.expression, prefix + "   |   |");
        System.out.println(prefix + "   |-size:");
        this.visit(lcRealloc.size, prefix + "       |");
        return null;
    }

    @Override
    public Object visitAutoTypeExpression(LCAutoTypeExpression lcAutoTypeExpression, Object prefix) {
        System.out.println(prefix + "-LCAutoTypeExpression" + (lcAutoTypeExpression.isErrorNode ? " **E**" : ""));
        return null;
    }

    @Override
    public Object visitNotNullAssert(LCNotNullAssert lcNotNullAssert, Object prefix) {
        System.out.println(prefix + "-LCNotNullAssert" + (lcNotNullAssert.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-base:");
        this.visit(lcNotNullAssert.base, prefix + "   |   |");
        System.out.println(prefix + "   |-theType: " + (lcNotNullAssert.theType != null ? lcNotNullAssert.theType : "<unknown>"));
        System.out.println(prefix + "   |-shouldBeLeftValue: " + lcNotNullAssert.shouldBeLeftValue);
        System.out.println(prefix + "   |-isLeftValue: " + lcNotNullAssert.isLeftValue);
        return null;
    }

    @Override
    public Object visitYield(LCYield lcYield, Object prefix) {
        System.out.println(prefix + "-LCYield" + (lcYield.isErrorNode ? " **E**" : ""));
        System.out.println(prefix + "   |-value:");
        this.visit(lcYield.value, prefix + "       |");
        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object prefix) {
        System.out.println(prefix + "-LCWith" + (lcWith.isErrorNode ? " **E**" : ""));
        if (lcWith.resources.length != 0) {
            System.out.println(prefix + "   |-resources:");
            for (LCStatement resource : lcWith.resources) {
                this.visit(resource, prefix + "   |   |");
            }
        }
        System.out.println(prefix + "   |-body:");
        this.visit(lcWith.body, prefix + "       |");

        return null;
    }
}
package ldk.l.lc.parser;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.base.LCModifier;
import ldk.l.lc.ast.expression.LCNewArray;
import ldk.l.lc.ast.expression.LCSwitchExpression;
import ldk.l.lc.ast.expression.type.*;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.error.ErrorStream;

public final class SyntaxChecker extends LCAstVisitor {
    private final ErrorStream errorStream;

    public SyntaxChecker(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (lcVariableDeclaration.typeExpression != null) {
            if (lcVariableDeclaration.typeExpression instanceof LCPredefinedTypeExpression predefinedTypeExpression && predefinedTypeExpression.keyword == Tokens.Type.Void) {
                System.err.println("Type of the variable cannot be 'void'.");
            }
            if (LCFlags.hasLateinit(lcVariableDeclaration.modifier.flags) && !(lcVariableDeclaration.typeExpression instanceof LCNullableTypeExpression)) {
                System.err.println("Cannot use 'lateinit' modifier on non-nullable type.");
            }
        } else {
            if (lcVariableDeclaration.init == null) {
                System.err.println("Variable declaration must have a type or an initializer.");
            }
        }
        return super.visitVariableDeclaration(lcVariableDeclaration, additional);
    }

    @Override
    public Object visitReferenceTypeExpression(LCReferenceTypeExpression lcReferenceTypeExpression, Object additional) {
        if (isVoidType(lcReferenceTypeExpression.base)) {
            System.err.println("Cannot put a '&' before the type 'void'.");
        }
        return super.visitReferenceTypeExpression(lcReferenceTypeExpression, additional);
    }

    @Override
    public Object visitArrayTypeExpression(LCArrayTypeExpression lcArrayTypeExpression, Object additional) {
        if (isVoidType(lcArrayTypeExpression.base)) {
            System.err.println("Cannot put a '[]' before the type 'void'.");
        }
        return super.visitArrayTypeExpression(lcArrayTypeExpression, additional);
    }

    @Override
    public Object visitNullableTypeExpression(LCNullableTypeExpression lcNullableTypeExpression, Object additional) {
        if (lcNullableTypeExpression.base instanceof LCNullableTypeExpression lcNullableTypeExpression2) {
            System.err.println("Cannot put a '?' before the type '" + lcNullableTypeExpression2.toTypeString() + "'.");
        } else if (isVoidType(lcNullableTypeExpression.base)) {
            System.err.println("Cannot put a '?' before the type 'void'.");
        }

        return super.visitNullableTypeExpression(lcNullableTypeExpression, additional);
    }

    @Override
    public Object visitSwitchExpression(LCSwitchExpression lcSwitchExpression, Object additional) {
        if (lcSwitchExpression.cases.length == 0) {
            System.err.println("switch 表达式中没有任何 case 子句");
        }
        return super.visitSwitchExpression(lcSwitchExpression, additional);
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (LCFlags.hasOperator(lcMethodDeclaration.modifier.flags) && !LCMethodDeclaration.operatorMethodNames.contains(lcMethodDeclaration.name)) {
            System.err.println("Unsupported operator method '" + lcMethodDeclaration.name + "'.");
        }
        return super.visitMethodDeclaration(lcMethodDeclaration, additional);
    }

    @Override
    public Object visitNewArray(LCNewArray lcNewArray, Object additional) {
        if ((lcNewArray.dimensions[0] == null) == (lcNewArray.elements == null)) {
            System.err.println("Either the dimensions or the elements of the new array must be specified.");
        }
        return super.visitNewArray(lcNewArray, additional);
    }

    @Override
    public Object visitModifier(LCModifier lcModifier, Object additional) {
        if (LCFlags.hasInternal(lcModifier.flags) && !LCFlags.hasConst(lcModifier.flags) && !LCFlags.hasFinal(lcModifier.flags)) {
            System.err.println("'internal' modifier can only be used with 'const' or 'final' modifiers.");
        }
        if (LCFlags.hasStatic(lcModifier.flags) && LCFlags.hasAbstract(lcModifier.flags)) {
            System.err.println("Cannot have both 'static' and 'abstract' modifiers.");
        }
        return null;
    }

    private boolean isVoidType(LCTypeExpression typeExpression) {
        return typeExpression instanceof LCPredefinedTypeExpression && ((LCPredefinedTypeExpression) typeExpression).keyword == Tokens.Type.Void;
    }
}

package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.*;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.token.Token;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.error.ErrorStream;

public final class LeftValueAttributor extends LCAstVisitor {
    private final ErrorStream errorStream;

    public LeftValueAttributor(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        if (Token.isAssignOperator(lcBinary._operator)) {
            this.visit(lcBinary.expression1, true);
            if (!lcBinary.expression1.isLeftValue) {
//                this.addError("Left child of operator "+ Op[LCBinary.op] + " need a left value",LCBinary.exp1);
            }

            this.visit(lcBinary.expression2, additional);
        } else {
            super.visitBinary(lcBinary, additional);
        }

        return null;
    }

    @Override
    public Object visitUnary(LCUnary lcUnary, Object shouldBeLeftValue) {
        if (shouldBeLeftValue instanceof Boolean && ((boolean) shouldBeLeftValue)) {
            System.err.println("LCUnary LCExpression cannot be left value.");
        }

        if (lcUnary._operator == Tokens.Operator.Inc || lcUnary._operator == Tokens.Operator.Dec) {
            this.visit(lcUnary.expression, true);

            if (!lcUnary.expression.isLeftValue) {
                System.err.println("LCUnary operator '" + lcUnary._operator.getCode() + "' can only be applied to a left value");
            }
        } else {
            super.visitUnary(lcUnary, null);
        }

        return null;
    }

    @Override
    public Object visitArrayAccess(LCArrayAccess lcArrayAccess, Object shouldBeLeftValue) {
        this.visit(lcArrayAccess.base, null);
        this.visit(lcArrayAccess.index, null);

        if (shouldBeLeftValue instanceof Boolean && ((boolean) shouldBeLeftValue)) {
            lcArrayAccess.isLeftValue = true;
        }
        return null;
    }

    //    public Object visitDot(LCDot lcDot, Object shouldBeLeftValue) {
//        this.visit(lcDot.property, shouldBeLeftValue);
//
//        this.visit(lcDot.base, null);
//
//        if (shouldBeLeftValue instanceof Boolean && ((boolean) shouldBeLeftValue)) {
//            if (lcDot.property.isLeftValue) {
//                lcDot.isLeftValue = true;
//            } else {
//                this.addError("LCExpression '" + exp.toString() + "'can not be assigned, because it's is not a left value", exp);
//            }
//        }
//
//        return null;
//    }
    @Override
    public Object visitGetAddress(LCGetAddress lcGetAddress, Object additional) {
        super.visitGetAddress(lcGetAddress, additional);
        if (lcGetAddress.paramTypeExpressions == null) {
            lcGetAddress.expression.isLeftValue = true;
        }
        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object shouldBeLeftValue) {
        if (shouldBeLeftValue instanceof Boolean booleanValue)
            lcVariable.isLeftValue = booleanValue;

        return null;
    }

    @Override
    public Object visitThis(LCThis lcThis, Object shouldBeLeftValue) {
        if (shouldBeLeftValue instanceof Boolean && ((boolean) shouldBeLeftValue))
            lcThis.isLeftValue = true;

        return null;
    }

    @Override
    public Object visitDereference(LCDereference lcDereference, Object shouldBeLeftValue) {
        this.visit(lcDereference.expression, false);
        if (shouldBeLeftValue instanceof Boolean booleanValue)
            lcDereference.isLeftValue = booleanValue;
        return null;
    }
}

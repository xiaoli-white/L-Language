package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

public final class LCBinary extends LCExpression {
    public Tokens.Operator _operator;
    public LCExpression expression1;
    public LCExpression expression2;
    public MethodSymbol methodSymbol = null;

    public LCBinary(Tokens.Operator _operator, LCExpression expression1, LCExpression expression2) {
        this(_operator, expression1, expression2, false);
    }

    public LCBinary(Tokens.Operator _operator, LCExpression expression1, LCExpression expression2, boolean isErrorNode) {
        super(new Position(expression1.position.beginPos(), expression2.position.endPos(), expression1.position.beginLine(), expression2.position.endLine(), expression1.position.beginCol(), expression2.position.endCol()), isErrorNode);
        this._operator = _operator;
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression1.parentNode = this;
        this.expression2.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitBinary(this, additional);
    }

    @Override
    public String toString() {
        return "LCBinary{" +
                "_operator=" + _operator +
                ", expression1=" + expression1 +
                ", expression2=" + expression2 +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
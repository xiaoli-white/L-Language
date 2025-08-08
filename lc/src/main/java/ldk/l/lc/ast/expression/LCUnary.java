package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

public final class LCUnary extends LCExpression {
    public Tokens.Operator _operator;
    public boolean isPrefix;
    public LCExpression expression;
    public MethodSymbol methodSymbol = null;

    public LCUnary(Tokens.Operator _operator, boolean isPrefix, LCExpression expression, Position pos) {
        this(_operator, isPrefix, expression, pos, false);
    }

    public LCUnary(Tokens.Operator _operator, boolean isPrefix, LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this._operator = _operator;
        this.isPrefix = isPrefix;
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitUnary(this, additional);
    }

    @Override
    public String toString() {
        return "LCUnary{" +
                "_operator=" + _operator +
                ", isPrefix=" + isPrefix +
                ", expression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
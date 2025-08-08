package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

public final class LCIn extends LCExpression {
    public LCExpression expression1;
    public LCExpression expression2;
    public MethodSymbol symbol = null;

    public LCIn(LCExpression expression1, LCExpression expression2, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression1 = expression1;
        this.expression1.parentNode = this;

        this.expression2 = expression2;
        this.expression2.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitIn(this, additional);
    }

    @Override
    public String toString() {
        return "LCIn{" +
                "expression1=" + expression1 +
                ", expression2=" + expression2 +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

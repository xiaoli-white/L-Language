package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCIs extends LCExpression {
    public LCExpression expression1;
    public LCExpression expression2;

    public LCIs(LCExpression expression1, LCExpression expression2, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression1 = expression1;
        this.expression1.parentNode = this;

        this.expression2 = expression2;
        this.expression2.parentNode = this;

        this.theType = SystemTypes.BOOLEAN;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitIs(this, additional);
    }

    @Override
    public String toString() {
        return "LCIs{" +
                "expression1=" + expression1 +
                ", expression2=" + expression2 +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCIs clone() throws CloneNotSupportedException {
        return new LCIs(expression1.clone(), expression2.clone(), position.clone(), isErrorNode);
    }
}

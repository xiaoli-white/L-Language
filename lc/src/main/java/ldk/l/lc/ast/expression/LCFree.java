package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCFree extends LCExpression {
    public LCExpression expression;

    public LCFree(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitFree(this, additional);
    }

    @Override
    public String toString() {
        return "LCFree{" +
                "expression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCFree clone() throws CloneNotSupportedException {
        return new LCFree(expression.clone(), position.clone(), isErrorNode);
    }
}

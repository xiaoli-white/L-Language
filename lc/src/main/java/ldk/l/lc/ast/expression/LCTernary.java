package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public final class LCTernary extends LCExpression {
    public LCExpression condition;
    public LCExpression then;
    public LCExpression _else;

    public LCTernary(LCExpression condition, LCExpression then, LCExpression _else, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.condition = condition;
        this.condition.parentNode = this;

        this.then = then;
        this.then.parentNode = this;

        this._else = _else;
        this._else.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTernary(this, additional);
    }

    @Override
    public String toString() {
        return "LCTernary{" +
                "condition=" + condition +
                ", then=" + then +
                ", _else=" + _else +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCTernary clone() throws CloneNotSupportedException {
        return new LCTernary(condition.clone(), then.clone(), _else.clone(), position.clone(), isErrorNode);
    }
}

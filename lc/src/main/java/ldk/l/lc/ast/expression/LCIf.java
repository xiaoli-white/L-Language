package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionWithScope;
import ldk.l.lc.util.Position;
import ldk.l.lc.ast.base.LCStatement;

public class LCIf extends LCExpressionWithScope {
    public LCExpression condition;
    public LCStatement then;
    public LCStatement _else;

    public LCIf(LCExpression condition, LCStatement then, LCStatement _else, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.condition = condition;
        this.then = then;
        this._else = _else;
        this.condition.parentNode = this;
        this.then.parentNode = this;

        if (this._else != null)
            this._else.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitIf(this, additional);
    }

    @Override
    public String toString() {
        return "LCIf{" +
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
    public LCIf clone() throws CloneNotSupportedException {
        return new LCIf(condition.clone(), then.clone(), _else != null ? _else.clone() : null, position.clone(), isErrorNode);
    }
}
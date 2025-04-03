package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCParenthesizedTypeExpression extends LCTypeExpression {
    public LCTypeExpression base;

    public LCParenthesizedTypeExpression(LCTypeExpression base, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.base.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitParenthesizedTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCParenthesizedTypeExpression{" +
                "base=" + base +
                ", theType=" + theType +
                ", isLeftValue=" + isLeftValue +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public String toTypeString() {
        return "(" + base.toTypeString() + ")";
    }

    @Override
    public LCParenthesizedTypeExpression clone() throws CloneNotSupportedException {
        return new LCParenthesizedTypeExpression(this.base.clone(), this.position.clone(), this.isErrorNode);
    }
}

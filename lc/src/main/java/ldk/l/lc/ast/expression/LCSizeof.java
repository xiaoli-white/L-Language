package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCSizeof extends LCExpression {
    public LCExpression expression;

    public LCSizeof(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;

        this.theType = SystemTypes.UNSIGNED_LONG;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSizeof(this, additional);
    }

    @Override
    public String toString() {
        return "LCSizeof{" +
                "expression=" + expression +
                ", theType=" + theType +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCSizeof clone() throws CloneNotSupportedException {
        return new LCSizeof(expression.clone(), position.clone(), isErrorNode);
    }
}

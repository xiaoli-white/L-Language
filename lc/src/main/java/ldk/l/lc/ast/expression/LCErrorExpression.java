package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCErrorExpression extends LCExpression {
    public LCExpression expression;

    public LCErrorExpression(Position pos) {
        this(null, pos);
    }

    public LCErrorExpression(LCExpression expression, Position pos) {
        super(pos, true);
        this.expression = expression;
        if (this.expression != null) this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitErrorExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCErrorExpression{" +
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
    public LCErrorExpression clone() throws CloneNotSupportedException {
        return new LCErrorExpression(expression != null ? expression.clone() : null, position.clone());
    }
}
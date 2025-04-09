package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Arrays;

public class LCExpressionStatement extends LCStatement {
    public LCExpression expression;

    public LCExpressionStatement(LCExpression expression, Position pos) {
        this(expression, pos, false);
    }

    public LCExpressionStatement(LCExpression expression,Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitExpressionStatement(this, additional);
    }

    @Override
    public String toString() {
        return "LCExpressionStatement{" +
                "expression=" + expression +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCExpressionStatement clone() throws CloneNotSupportedException {
        return new LCExpressionStatement(this.expression.clone(), this.position.clone(), this.isErrorNode);
    }
}
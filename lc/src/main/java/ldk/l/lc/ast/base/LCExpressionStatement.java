package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCExpressionStatement extends LCStatement {
    public LCExpression expression;
    public boolean hasSemiColon;

    public LCExpressionStatement(LCExpression expression, boolean hasSemiColon, Position pos) {
        this(expression, hasSemiColon, pos, false);
    }

    public LCExpressionStatement(LCExpression expression, boolean hasSemiColon, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.hasSemiColon = hasSemiColon;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitExpressionStatement(this, additional);
    }

    @Override
    public String toString() {
        return "LCExpressionStatement{" +
                "expression=" + expression +
                ", hasSemiColon=" + hasSemiColon +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCExpressionStatement clone() throws CloneNotSupportedException {
        return new LCExpressionStatement(this.expression.clone(), this.hasSemiColon, this.position.clone(), this.isErrorNode);
    }
}
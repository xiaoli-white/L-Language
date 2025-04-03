package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCDelete extends LCExpression {
    public LCExpression expression;

    public LCDelete(LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitDelete(this, additional);
    }

    @Override
    public String toString() {
        return "LCDelete{" +
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
    public LCDelete clone() throws CloneNotSupportedException {
        LCDelete lcDelete = new LCDelete(expression.clone(), position.clone(), isErrorNode);
        lcDelete.theType = theType;
        lcDelete.isLeftValue = isLeftValue;
        lcDelete.shouldBeLeftValue = shouldBeLeftValue;
        lcDelete.constValue = constValue;
        return lcDelete;
    }
}

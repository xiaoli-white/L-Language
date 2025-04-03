package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCEmptyExpression extends LCExpression {
    public LCEmptyExpression(Position pos) {
        super(pos, false);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitEmptyExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCEmptyExpression";
    }

    @Override
    public LCEmptyExpression clone() throws CloneNotSupportedException {
        LCEmptyExpression lcEmptyExpression = new LCEmptyExpression(position.clone());
        lcEmptyExpression.theType = theType;
        lcEmptyExpression.isLeftValue = isLeftValue;
        lcEmptyExpression.shouldBeLeftValue = shouldBeLeftValue;
        lcEmptyExpression.constValue = constValue;
        return lcEmptyExpression;
    }
}

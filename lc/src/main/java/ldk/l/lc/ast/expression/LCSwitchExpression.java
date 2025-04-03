package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionWithScope;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCSwitchExpression extends LCExpressionWithScope {
    public LCExpression selector;
    public LCCase[] cases;

    public LCSwitchExpression(LCExpression selector, LCCase[] cases, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.selector = selector;
        this.selector.parentNode = this;

        this.cases = cases;
        for (LCCase lcCase : this.cases) lcCase.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSwitchExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCSwitchExpression{" +
                "selector=" + selector +
                ", cases=" + Arrays.toString(cases) +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCSwitchExpression clone() throws CloneNotSupportedException {
        return new LCSwitchExpression(selector.clone(), Arrays.copyOf(cases, cases.length), position.clone(), isErrorNode);
    }
}

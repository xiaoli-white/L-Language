package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionWithScope;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;

public final class LCSwitchExpression extends LCExpressionWithScope {
    public LCExpression selector;
    public List<LCCase> cases;

    public LCSwitchExpression(LCExpression selector, List<LCCase> cases, Position pos, boolean isErrorNode) {
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
                ", cases=" + cases +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

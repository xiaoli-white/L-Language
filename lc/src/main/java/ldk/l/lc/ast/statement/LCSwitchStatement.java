package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.ast.expression.LCCase;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;

public final class LCSwitchStatement extends LCStatementWithScope {
    public LCExpression selector;
    public List<LCCase> cases;

    public LCSwitchStatement(LCExpression selector, List<LCCase> cases, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.selector = selector;
        this.selector.parentNode = this;

        this.cases = cases;
        for (LCCase lcCase : this.cases) lcCase.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSwitchStatement(this, additional);
    }

    @Override
    public String toString() {
        return "LCSwitchStatement{" +
                "selector=" + selector +
                ", cases=" + cases +
                ", scope=" + scope +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

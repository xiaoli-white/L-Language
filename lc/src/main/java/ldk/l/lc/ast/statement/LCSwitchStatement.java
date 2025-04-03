package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.ast.expression.LCCase;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCSwitchStatement extends LCStatementWithScope {
    public LCExpression selector;
    public LCCase[] cases;

    public LCSwitchStatement(LCExpression selector, LCCase[] cases, Position pos, boolean isErrorNode) {
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
                ", cases=" + Arrays.toString(cases) +
                ", scope=" + scope +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCSwitchStatement clone() throws CloneNotSupportedException {
        return new LCSwitchStatement(selector.clone(), Arrays.copyOf(cases, cases.length), position.clone(), isErrorNode);
    }
}

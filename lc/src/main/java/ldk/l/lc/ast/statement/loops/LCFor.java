package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;
import ldk.l.lc.ast.base.LCStatement;

import java.util.Arrays;

public class LCFor extends LCAbstractLoop {
    public LCStatement init;
    public LCExpression condition;
    public LCExpression increment;
    public LCStatement body;

    public LCFor(LCStatement init, LCExpression condition, LCExpression increment, LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;

        if (this.init != null) this.init.parentNode = this;
        if (this.condition != null) this.condition.parentNode = this;
        if (this.increment != null) this.increment.parentNode = this;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitFor(this, additional);
    }

    @Override
    public String toString() {
        return "LCFor{" +
                "init=" + init +
                ", condition=" + condition +
                ", increment=" + increment +
                ", body=" + body +
                ", scope=" + scope +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCFor clone() throws CloneNotSupportedException {
        return new LCFor(init != null ? init.clone() : null, condition != null ? condition.clone() : null, increment != null ? increment.clone() : null, body.clone(), position.clone(), isErrorNode);
    }
}
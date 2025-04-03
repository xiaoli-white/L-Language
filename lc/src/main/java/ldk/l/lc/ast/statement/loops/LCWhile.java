package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

public class LCWhile extends LCAbstractLoop {
    public LCExpression condition;
    public LCStatement body;

    public LCWhile(LCExpression condition, LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.condition = condition;
        this.body = body;

        this.condition.parentNode = this;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitWhile(this, additional);
    }

    @Override
    public String toString() {
        return "LCWhile{" +
                "condition=" + condition +
                ", body=" + body +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCWhile clone() throws CloneNotSupportedException {
        return new LCWhile(condition.clone(), body.clone(), position.clone(), isErrorNode);
    }
}

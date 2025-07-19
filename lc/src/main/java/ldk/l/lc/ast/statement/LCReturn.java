package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;
import ldk.l.lc.ast.base.LCExpression;

public final class LCReturn extends LCStatement {
    public LCExpression returnedValue;

    public LCReturn(LCExpression returnedValue, Position pos) {
        this(returnedValue, pos, false);
    }

    public LCReturn(LCExpression returnedValue, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.returnedValue = returnedValue;
        if (this.returnedValue != null) this.returnedValue.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitReturn(this, additional);
    }

    @Override
    public String toString() {
        return "LCReturn{" +
                "returnedValue=" + returnedValue +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCReturn clone() throws CloneNotSupportedException {
        return new LCReturn(this.returnedValue != null ? this.returnedValue.clone() : null, this.position.clone(), this.isErrorNode);
    }
}
package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCErrorStatement extends LCStatement {
    public LCStatement statement;

    public LCErrorStatement(Position pos) {
        this(null, pos);
    }

    public LCErrorStatement(LCStatement statement, Position pos) {
        super(pos, true);
        this.statement = statement;
        if (this.statement != null) this.statement.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitErrorStatement(this, additional);
    }

    @Override
    public String toString() {
        return "LCErrorStatement{" +
                "LCStatement=" + statement +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCErrorStatement clone() throws CloneNotSupportedException {
        return new LCErrorStatement(statement != null ? statement.clone() : null, position.clone());
    }
}
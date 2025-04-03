package ldk.l.lc.ast.base;


import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCBlock extends LCExpressionWithScope {
    public LCStatement[] statements;

    public LCBlock(LCStatement[] statements, Position pos) {
        this(statements, pos, false);
    }

    public LCBlock(LCStatement[] statements, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.statements = statements;
        for (LCStatement statement : statements) statement.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitBlock(this, additional);
    }

    @Override
    public String toString() {
        return "LCBlock{" +
                "statements=" + Arrays.toString(statements) +
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
    public LCBlock clone() throws CloneNotSupportedException {
        return new LCBlock(Arrays.copyOf(this.statements, this.statements.length), this.position.clone(), this.isErrorNode);
    }
}
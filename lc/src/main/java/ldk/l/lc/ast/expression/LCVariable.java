package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.VariableSymbol;

public final class LCVariable extends LCExpression {
    public String name;
    public VariableSymbol symbol = null;

    public LCVariable(String name, Position pos) {
        this(name, pos, false);
    }

    public LCVariable(String name, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitVariable(this, additional);
    }

    @Override
    public String toString() {
        return "LCVariable{" +
                "name='" + name + '\'' +
                ", symbol=" + symbol +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
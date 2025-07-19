package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

public final class LCSuper extends LCExpression {
    public ObjectSymbol symbol = null;

    public LCSuper(Position pos) {
        this(pos, false);
    }

    public LCSuper(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSuper(this, additional);
    }

    @Override
    public String toString() {
        return "LCSuper{" +
                "symbol=" + symbol +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCSuper clone() throws CloneNotSupportedException {
        LCSuper lcSuper = new LCSuper(position.clone(), isErrorNode);
        lcSuper.symbol = symbol;
        return lcSuper;
    }
}
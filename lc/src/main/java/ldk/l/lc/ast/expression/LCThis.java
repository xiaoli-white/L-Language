package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.Objects;

public class LCThis extends LCExpression {
    public ObjectSymbol symbol = null;

    public LCThis(Position pos) {
        this(pos, false);
    }

    public LCThis(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitThis(this, additional);
    }

    @Override
    public String toString() {
        return "LCThis{" +
                "symbol=" + symbol +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCThis clone() throws CloneNotSupportedException {
        LCThis lcThis = new LCThis(position.clone(), isErrorNode);
        lcThis.symbol = symbol;
        return lcThis;
    }

}
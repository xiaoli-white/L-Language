package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

public final class LCArrayAccess extends LCExpression {
    public LCExpression base;
    public LCExpression index;
    public MethodSymbol methodSymbol = null;

    public LCArrayAccess(LCExpression base, LCExpression index, Position pos) {
        this(base, index, pos, false);
    }

    public LCArrayAccess(LCExpression base, LCExpression index, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.base = base;
        this.index = index;
        this.base.parentNode = this;
        this.index.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitArrayAccess(this, additional);
    }

    @Override
    public String toString() {
        return "LCArrayAccess{" +
                "base=" + base +
                ", index=" + index +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCArrayAccess clone() throws CloneNotSupportedException {
        return new LCArrayAccess(base.clone(), index.clone(), position.clone(), isErrorNode);
    }
}
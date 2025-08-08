package ldk.l.lc.ast.expression.literal;

import ldk.l.lc.util.ConstValue;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public abstract sealed class LCLiteral<T> extends LCExpression permits LCStringLiteral, LCIntegerLiteral, LCDecimalLiteral, LCBooleanLiteral, LCCharLiteral, LCNullLiteral, LCNullptrLiteral {
    public T value;

    protected LCLiteral(T value, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.value = value;
        this.constValue = new ConstValue(value);
    }

    @Override
    public String toString() {
        return this.value + ": " + (this.theType != null ? this.theType : "<unknown>");
    }
}
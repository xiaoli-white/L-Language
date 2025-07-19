package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

public abstract sealed class LCTypeExpression extends LCExpression permits LCArrayTypeExpression, LCAutoTypeExpression, LCMethodPointerTypeExpression, LCNullableTypeExpression, LCParenthesizedTypeExpression, LCPointerTypeExpression, LCPredefinedTypeExpression, LCReferenceTypeExpression, LCTypeReferenceExpression {
    public LCTypeExpression(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public abstract String toTypeString();

    @Override
    public abstract LCTypeExpression clone() throws CloneNotSupportedException;
}
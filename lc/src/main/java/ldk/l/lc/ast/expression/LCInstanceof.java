package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;

public final class LCInstanceof extends LCExpression {
    public LCExpression expression;
    public LCTypeReferenceExpression typeExpression;

    public LCInstanceof(LCExpression expression, LCTypeReferenceExpression typeExpression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.typeExpression = typeExpression;
        this.typeExpression.parentNode = this;

        this.theType = SystemTypes.BOOLEAN;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitInstanceof(this, additional);
    }

    @Override
    public String toString() {
        return "LCInstanceof{" +
                "expression=" + expression +
                ", typeExpression=" + typeExpression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCInstanceof clone() throws CloneNotSupportedException {
        return new LCInstanceof(expression.clone(), typeExpression.clone(), position.clone(), isErrorNode);
    }
}

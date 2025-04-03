package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;

public class LCClassof extends LCExpression {
    public LCTypeReferenceExpression typeExpression;

    public LCClassof(LCTypeReferenceExpression typeExpression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.typeExpression = typeExpression;
        if (this.typeExpression != null) this.typeExpression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitClassof(this, additional);
    }

    @Override
    public String toString() {
        return "LCClassof{" +
                "typeExpression=" + typeExpression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCClassof clone() throws CloneNotSupportedException {
        return new LCClassof(typeExpression.clone(), position.clone(), isErrorNode);
    }
}

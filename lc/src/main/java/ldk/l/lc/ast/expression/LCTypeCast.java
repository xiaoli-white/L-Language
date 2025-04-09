package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;

public final class LCTypeCast extends LCExpression {
    public Kind kind;
    public LCTypeExpression typeExpression;
    public LCExpression expression;

    public LCTypeCast(Kind kind, LCTypeExpression typeExpression, LCExpression expression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.kind = kind;

        this.typeExpression = typeExpression;
        this.typeExpression.parentNode = this;

        this.expression = expression;
        this.expression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTypeCast(this, additional);
    }

    @Override
    public String toString() {
        return "LCTypeCast{" +
                "kind=" + kind +
                ", typeExpression=" + typeExpression +
                ", expression=" + expression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCTypeCast clone() throws CloneNotSupportedException {
        return new LCTypeCast(kind, typeExpression.clone(), expression.clone(), position.clone(), isErrorNode);
    }

    public enum Kind {
        STATIC,
        DYNAMIC,
        REINTERPRET
    }
}

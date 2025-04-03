package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCTypedef extends LCStatement {
    public LCTypeExpression typeExpression;
    public String name;

    public LCTypedef(LCTypeExpression typeExpression, String name, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.typeExpression = typeExpression;
        this.typeExpression.parentNode = this;

        this.name = name;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTypedef(this, additional);
    }

    @Override
    public String toString() {
        return "LCTypedef{" +
                "LCTypeExpression=" + typeExpression +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCTypedef clone() throws CloneNotSupportedException {
        return new LCTypedef(typeExpression.clone(), name, position.clone(), isErrorNode);
    }
}

package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class LCTypeReferenceExpression extends LCTypeExpression {
    public String name;
    public List<LCTypeExpression> typeArgs = null;

    public LCTypeReferenceExpression(String name, Position pos) {
        this(name, pos, false);
    }

    public LCTypeReferenceExpression(String name, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTypeReferenceExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCTypeReferenceExpression{" +
                "name='" + name + '\'' +
                ", typeArgs=" + typeArgs +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    public void setTypeArgs(List<LCTypeExpression> typeArgs) {
        this.typeArgs = typeArgs;
        for (LCTypeExpression typeExpression : this.typeArgs) typeExpression.parentNode = this;
    }

    @Override
    public String toTypeString() {
        StringBuilder result = new StringBuilder(name);
        if (typeArgs != null) {
            result.append("<").append(typeArgs.stream().map(LCTypeExpression::toTypeString).collect(Collectors.joining(", "))).append(">");
        }
        return result.toString();
    }

    @Override
    public LCTypeReferenceExpression clone() throws CloneNotSupportedException {
        LCTypeReferenceExpression lcTypeReferenceExpression = new LCTypeReferenceExpression(this.name, this.position.clone(), this.isErrorNode);
        lcTypeReferenceExpression.typeArgs = this.typeArgs != null ? new ArrayList<>(this.typeArgs) : null;
        return lcTypeReferenceExpression;
    }
}

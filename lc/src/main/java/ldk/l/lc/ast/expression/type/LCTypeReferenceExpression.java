package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCTypeReferenceExpression extends LCTypeExpression {
    public String name;
    public LCTypeExpression[] typeArgs = null;

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
                ", typeArgs=" + Arrays.toString(typeArgs) +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    public void setTypeArgs(LCTypeExpression[] typeArgs) {
        this.typeArgs = typeArgs;
        for (LCTypeExpression typeExpression : this.typeArgs) typeExpression.parentNode = this;
    }

    @Override
    public String toTypeString() {
        StringBuilder result = new StringBuilder(name);
        if (typeArgs != null) {
            result.append("<");
            for (int i = 0; i < typeArgs.length; i++) {
                result.append(typeArgs[i].toTypeString());
                if (i < typeArgs.length - 1) {
                    result.append(", ");
                }
            }
            result.append(">");
        }
        return result.toString();
    }

    @Override
    public LCTypeReferenceExpression clone() throws CloneNotSupportedException {
        LCTypeReferenceExpression lcTypeReferenceExpression = new LCTypeReferenceExpression(this.name, this.position.clone(), this.isErrorNode);
        lcTypeReferenceExpression.typeArgs = this.typeArgs != null ? Arrays.copyOf(this.typeArgs, this.typeArgs.length) : null;
        return lcTypeReferenceExpression;
    }
}

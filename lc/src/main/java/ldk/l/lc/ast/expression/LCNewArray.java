package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.semantic.types.ArrayType;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCNewArray extends LCExpression {
    public LCExpression place;
    public LCTypeExpression typeExpression;
    public LCExpression[] dimensions;
    public LCExpression[] elements;

    public LCNewArray(LCExpression place, LCTypeExpression typeExpression, LCExpression[] dimensions, LCExpression[] elements, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.place = place;
        if (this.place != null) this.place.parentNode = this;
        this.typeExpression = typeExpression;
        this.typeExpression.parentNode = this;
        this.dimensions = dimensions;
        for (LCExpression dimension : dimensions) if (dimension != null) dimension.parentNode = this;
        this.elements = elements;
        if (this.elements != null) for (LCExpression element : elements) element.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNewArray(this, additional);
    }

    @Override
    public String toString() {
        return "LCNewArray{" +
                "place=" + place +
                ", typeExpression=" + typeExpression +
                ", dimensions=" + Arrays.toString(dimensions) +
                ", elements=" + Arrays.toString(elements) +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCNewArray clone() throws CloneNotSupportedException {
        LCNewArray node = new LCNewArray(this.place != null ? this.place.clone() : null, this.typeExpression.clone(), this.dimensions.clone(), this.elements != null ? this.elements.clone() : null, this.position.clone(), this.isErrorNode);
        node.parentNode = this.parentNode;
        return node;
    }

    public Type getRealType() {
        Type type = typeExpression.theType;
        for (int i = 1; i < dimensions.length; i++) {
            type = new ArrayType(type);
        }
        return type;
    }
}

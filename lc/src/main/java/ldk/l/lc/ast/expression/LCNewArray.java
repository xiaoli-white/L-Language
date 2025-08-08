package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.semantic.types.ArrayType;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;

public final class LCNewArray extends LCExpression {
    public LCExpression place;
    public LCTypeExpression typeExpression;
    public List<LCExpression> dimensions;
    public List<LCExpression> elements;

    public LCNewArray(LCExpression place, LCTypeExpression typeExpression, List<LCExpression> dimensions, List<LCExpression> elements, Position pos, boolean isErrorNode) {
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
                ", dimensions=" + dimensions +
                ", elements=" + elements +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    public Type getRealType() {
        Type type = typeExpression.theType;
        for (int i = 1; i < dimensions.size(); i++) {
            type = new ArrayType(type);
        }
        return type;
    }
}

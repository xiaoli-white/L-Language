package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCNewObject extends LCExpression {
    public LCExpression place;
    public LCTypeExpression typeExpression;
    public List<LCExpression> arguments;
    public MethodSymbol constructorSymbol = null;

    public LCNewObject(LCExpression place, LCTypeExpression typeExpression, List<LCExpression> arguments, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.place = place;
        if (this.place != null) this.place.parentNode = this;

        this.typeExpression = typeExpression;
        this.typeExpression.parentNode = this;

        this.arguments = arguments;
        for (LCExpression argument : arguments) argument.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitNewObject(this, additional);
    }

    @Override
    public String toString() {
        return "LCNewObject{" +
                "place=" + place +
                ", typeExpression=" + typeExpression +
                ", arguments=" + arguments +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCNewObject clone() throws CloneNotSupportedException {
        LCNewObject lcNewObject = new LCNewObject(place != null ? place.clone() : null, typeExpression.clone(), new ArrayList<>(arguments), position.clone(), isErrorNode);
        lcNewObject.theType = theType;
        lcNewObject.isLeftValue = isLeftValue;
        lcNewObject.shouldBeLeftValue = shouldBeLeftValue;
        lcNewObject.constValue = constValue;
        return lcNewObject;
    }
}

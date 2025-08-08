package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCParameterList;
import ldk.l.lc.util.Position;

import java.util.stream.Collectors;

public final class LCMethodPointerTypeExpression extends LCTypeExpression {
    public LCParameterList parameterList;
    public LCTypeExpression returnTypeExpression;

    public LCMethodPointerTypeExpression(LCParameterList parameterList, LCTypeExpression returnTypeExpression, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.parameterList = parameterList;
        this.parameterList.parentNode = this;

        this.returnTypeExpression = returnTypeExpression;
        if (this.returnTypeExpression != null) this.returnTypeExpression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitMethodPointerTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCMethodPointerTypeExpression{" +
                "parameterList=" + parameterList +
                ", returnTypeExpression=" + returnTypeExpression +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public String toTypeString() {
        return "(" + parameterList.parameters.stream().map(parameter -> parameter.typeExpression.toTypeString()).collect(Collectors.joining(", ")) +
                "):" + returnTypeExpression.toTypeString();
    }
}

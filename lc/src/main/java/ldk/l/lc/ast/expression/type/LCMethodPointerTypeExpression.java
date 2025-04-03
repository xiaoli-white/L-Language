package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCParameterList;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCMethodPointerTypeExpression extends LCTypeExpression {
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
        StringBuilder result = new StringBuilder("(");
        for (int i = 0; i < parameterList.parameters.length; i++) {
            LCVariableDeclaration parameter = parameterList.parameters[i];
            if (parameter.typeExpression == null) continue;

            result.append(parameter.typeExpression.toTypeString());
            if (i < parameterList.parameters.length - 1) {
                result.append(", ");
            }
        }
        result.append("):").append(returnTypeExpression.toTypeString());
        return result.toString();
    }

    @Override
    public LCMethodPointerTypeExpression clone() throws CloneNotSupportedException {
        return new LCMethodPointerTypeExpression(this.parameterList.clone(), this.returnTypeExpression.clone(), this.position.clone(), this.isErrorNode);
    }
}

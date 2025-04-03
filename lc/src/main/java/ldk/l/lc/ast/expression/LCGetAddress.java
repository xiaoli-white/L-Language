package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.Arrays;
import java.util.Objects;

public class LCGetAddress extends LCExpression {
    public LCExpression expression;
    public String name;
    public LCTypeExpression[] paramTypeExpressions;
    public MethodSymbol methodSymbol;

    public LCGetAddress(LCExpression expression, Position pos, boolean isErrorNode) {
        this(expression, null, null, pos, isErrorNode);
    }

    public LCGetAddress(LCExpression expression, String name, LCTypeExpression[] paramTypeExpressions, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.name = name;
        this.paramTypeExpressions = paramTypeExpressions;
        if (paramTypeExpressions != null)
            for (LCTypeExpression paramTypeExpression : paramTypeExpressions) paramTypeExpression.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitGetAddress(this, additional);
    }

    @Override
    public String toString() {
        return "LCGetAddress{" +
                "expression=" + expression +
                ", name='" + name + '\'' +
                ", paramTypeExpressions=" + Arrays.toString(paramTypeExpressions) +
                ", methodSymbol=" + methodSymbol +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCGetAddress clone() throws CloneNotSupportedException {
        return new LCGetAddress(expression.clone(), name, paramTypeExpressions != null ? paramTypeExpressions.clone() : null, position.clone(), isErrorNode);
    }
}

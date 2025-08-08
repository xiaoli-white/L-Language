package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCGetAddress extends LCExpression {
    public LCExpression expression;
    public String name;
    public List<LCTypeExpression> parameterTypeExpressions;
    public MethodSymbol methodSymbol;

    public LCGetAddress(LCExpression expression, Position pos, boolean isErrorNode) {
        this(expression, null, null, pos, isErrorNode);
    }

    public LCGetAddress(LCExpression expression, String name, List<LCTypeExpression> parameterTypeExpressions, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.name = name;
        if (parameterTypeExpressions != null) {
            this.parameterTypeExpressions = new ArrayList<>(parameterTypeExpressions);
            for (LCTypeExpression paramTypeExpression : parameterTypeExpressions) paramTypeExpression.parentNode = this;
        }
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
                ", parameterTypeExpressions=" + parameterTypeExpressions +
                ", methodSymbol=" + methodSymbol +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

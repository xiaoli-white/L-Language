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
    public List<LCTypeExpression> paramTypeExpressions;
    public MethodSymbol methodSymbol;

    public LCGetAddress(LCExpression expression, Position pos, boolean isErrorNode) {
        this(expression, null, null, pos, isErrorNode);
    }

    public LCGetAddress(LCExpression expression, String name, List<LCTypeExpression> paramTypeExpressions, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.expression = expression;
        this.expression.parentNode = this;
        this.name = name;
        if (paramTypeExpressions != null) {
            this.paramTypeExpressions = new ArrayList<>(paramTypeExpressions);
            for (LCTypeExpression paramTypeExpression : paramTypeExpressions) paramTypeExpression.parentNode = this;
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
                ", paramTypeExpressions=" + paramTypeExpressions +
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
        return new LCGetAddress(expression.clone(), name, paramTypeExpressions != null ? new ArrayList<>(paramTypeExpressions) : null, position.clone(), isErrorNode);
    }
}

package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCMethodCall extends LCExpression {
    public String name;
    public LCExpression expression;
    public List<LCTypeExpression> typeArguments;
    public List<LCExpression> arguments;
    public MethodSymbol symbol = null;
    public Position positionOfName;

    public LCMethodCall(LCExpression expression, List<LCExpression> arguments, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.typeArguments = null;
        this.name = null;
        this.expression = expression;

        this.arguments = arguments;
        for (LCExpression v : this.arguments) v.parentNode = this;

        this.positionOfName = null;
    }

    public LCMethodCall(String name, Position positionOfName, List<LCTypeExpression> typeArguments, List<LCExpression> arguments, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
        this.expression = null;
        this.typeArguments = typeArguments;
        for (LCTypeExpression typeArgument : this.typeArguments) typeArgument.parentNode = this;

        this.arguments = arguments;
        for (LCExpression v : this.arguments) v.parentNode = this;

        this.positionOfName = positionOfName;
    }

    private LCMethodCall(String name, Position positionOfName, LCExpression expression, List<LCTypeExpression> typeArguments, List<LCExpression> arguments, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
        this.positionOfName = positionOfName;

        this.expression = expression;
        if (this.expression != null) this.expression.parentNode = this;

        this.typeArguments = typeArguments;
        if (this.typeArguments != null)
            for (LCTypeExpression typeArgument : this.typeArguments) typeArgument.parentNode = this;

        this.arguments = arguments;
        for (LCExpression v : this.arguments) v.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitMethodCall(this, additional);
    }

    @Override
    public String toString() {
        return "LCMethodCall{" +
                "name='" + name + '\'' +
                ", expression=" + expression +
                ", typeArguments=" + typeArguments +
                ", arguments=" + arguments +
                ", symbol=" + symbol +
                ", positionOfName=" + positionOfName +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
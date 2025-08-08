package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCLambda extends LCExpressionWithScope {
    public MethodSymbol symbol = null;
    public LCModifier modifier;
    public List<LCTypeParameter> typeParameters;
    public LCParameterList parameterList;
    public LCTypeExpression returnTypeExpression;
    public List<LCTypeReferenceExpression> threwExceptions;
    public LCStatement body;
    public Type returnType = SystemTypes.AUTO;

    public LCLambda(List<LCTypeParameter> typeParameters, LCParameterList parameterList, LCTypeExpression returnTypeExpression, boolean hasThisReadonly, List<LCTypeReferenceExpression> threwExceptions, LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.typeParameters = typeParameters;
        for (LCTypeParameter typeParameter : this.typeParameters) typeParameter.parentNode = this;

        this.parameterList = parameterList;
        this.parameterList.parentNode = this;
        this.returnTypeExpression = returnTypeExpression;
        if (this.returnTypeExpression != null) this.returnTypeExpression.parentNode = this;

        this.modifier = new LCModifier(hasThisReadonly ? LCFlags.THIS_READONLY : 0, Position.origin);

        this.threwExceptions = threwExceptions;
        for (LCTypeExpression LCTypeExpression : this.threwExceptions) LCTypeExpression.parentNode = this;

        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitLambda(this, additional);
    }

    @Override
    public String toString() {
        return "LCLambda{" +
                "symbol=" + symbol +
                ", modifier=" + modifier +
                ", typeParameters=" + typeParameters +
                ", callSignature=" + parameterList +
                ", returnTypeExpression=" + returnTypeExpression +
                ", threwExceptions=" + threwExceptions +
                ", body=" + body +
                ", returnType=" + returnType +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

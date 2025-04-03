package ldk.l.lc.ast.statement.declaration;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.MethodKind;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.*;

public class LCMethodDeclaration extends LCDeclaration {
    public static final List<String> operatorMethodNames = new ArrayList<>() {{
        add("contains");
        add("plus");
        add("minus");
        add("times");
        add("div");
        add("rem");
        add("and");
        add("or");
        add("xor");
        add("shl");
        add("shr");
        add("ushr");
        add("plusAssign");
        add("minusAssign");
        add("timesAssign");
        add("divAssign");
        add("remAssign");
        add("andAssign");
        add("orAssign");
        add("xorAssign");
        add("shlAssign");
        add("shrAssign");
        add("ushrAssign");
        add("equals");
        add("unaryPlus");
        add("unaryMinus");
        add("increase");
        add("decrease");
        add("not");
        add("inv");
        add("invoke");
        add("get");
        add("set");
    }};
    public Scope scope = null;
    public LCModifier modifier = null;
    public MethodKind methodKind;
    public String name;
    public LCTypeParameter[] typeParameters;
    public LCCallSignature callSignature;
    public LCTypeExpression returnTypeExpression;
    public long initialFlags;
    public LCTypeReferenceExpression[] threwExceptions;
    public LCTypeReferenceExpression extended;
    public LCBlock body;
    public MethodSymbol symbol = null;
    public Type returnType = SystemTypes.AUTO;

    public LCMethodDeclaration(MethodKind methodKind, String name, LCTypeParameter[] typeParameters, LCCallSignature callSignature, LCTypeExpression returnTypeExpression, long initialFlags, LCTypeReferenceExpression[] threwExceptions, LCTypeReferenceExpression extended) {
        super(Position.origin, true);
        this.methodKind = methodKind;
        this.name = name;

        this.typeParameters = typeParameters;
        for (LCTypeParameter typeParameter : this.typeParameters) typeParameter.parentNode = this;

        this.callSignature = callSignature;
        this.callSignature.parentNode = this;

        this.returnTypeExpression = returnTypeExpression;
        if (this.returnTypeExpression != null) this.returnTypeExpression.parentNode = this;

        this.initialFlags = initialFlags;

        this.threwExceptions = threwExceptions;
        for (LCTypeExpression LCTypeExpression : this.threwExceptions) LCTypeExpression.parentNode = this;

        this.extended = extended;
        if (this.extended != null) this.extended.parentNode = this;
    }

    public void init(LCBlock body, Position pos, boolean isErrorNode) {
        this.body = body;
        if (this.body != null) this.body.parentNode = this;

        this.position = pos;
        this.isErrorNode = isErrorNode;
    }

    public final void setModifier(LCModifier modifier) {
        this.modifier = modifier;
        this.modifier.flags |= this.initialFlags;
        this.modifier.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitMethodDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCMethodDeclaration{" +
                "scope=" + scope +
                ", modifier=" + modifier +
                ", methodKind=" + methodKind +
                ", name='" + name + '\'' +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", callSignature=" + callSignature +
                ", returnTypeExpression=" + returnTypeExpression +
                ", initialFlags=" + initialFlags +
                ", threwExceptions=" + Arrays.toString(threwExceptions) +
                ", extended=" + extended +
                ", body=" + body +
                ", symbol=" + symbol +
                ", returnType=" + returnType +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCMethodDeclaration clone() throws CloneNotSupportedException {
        LCMethodDeclaration lcMethodDeclaration = new LCMethodDeclaration(this.methodKind, this.name, Arrays.copyOf(this.typeParameters, this.typeParameters.length), this.callSignature.clone(), this.returnTypeExpression != null ? this.returnTypeExpression.clone() : null, this.initialFlags, Arrays.copyOf(this.threwExceptions, this.threwExceptions.length), this.extended.clone());
        lcMethodDeclaration.init(this.body.clone(), this.position, this.isErrorNode);
        return lcMethodDeclaration;
    }

    public static class LCCallSignature extends LCAstNode {
        public LCParameterList parameterList;

        public LCCallSignature(LCParameterList parameterList, Position pos) {
            this(parameterList, pos, false);
        }

        public LCCallSignature(LCParameterList parameterList, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.parameterList = parameterList;
            if (this.parameterList != null) this.parameterList.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitCallSignature(this, additional);
        }

        @Override
        public String toString() {
            return "LCCallSignature{" +
                    "parameterList=" + parameterList +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCCallSignature clone() throws CloneNotSupportedException {
            return new LCCallSignature(parameterList.clone(), position.clone(), isErrorNode);
        }
    }
}
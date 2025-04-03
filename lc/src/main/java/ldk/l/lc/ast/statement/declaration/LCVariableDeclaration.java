package ldk.l.lc.ast.statement.declaration;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.VariableSymbol;

import java.util.Arrays;

public class LCVariableDeclaration extends LCDeclaration {
    public LCAnnotationDeclaration.LCAnnotation[] annotations = null;
    public LCModifier modifier = null;
    public boolean isVal;
    public String name;
    public LCTypeExpression typeExpression;
    public LCTypeReferenceExpression extended;
    public LCExpression delegated;
    public LCExpression init;
    public LCMethodDeclaration getter;
    public LCMethodDeclaration setter;
    public boolean hasSemiColon;
    public Type theType = SystemTypes.AUTO;
    public VariableSymbol symbol = null;
    private static int unnamedVarCount = 0;

    public LCVariableDeclaration(boolean isVal, String name, LCTypeExpression typeExpression, LCExpression delegated, LCExpression init, LCMethodDeclaration getter, LCMethodDeclaration setter, boolean hasSemiColon, Position pos, boolean isErrorNode) {
        this(isVal, name, typeExpression, null, delegated, init, getter, setter, hasSemiColon, pos, isErrorNode);
    }

    public LCVariableDeclaration(boolean isVal, String name, LCTypeExpression typeExpression, LCTypeReferenceExpression extended, LCExpression delegated, LCExpression init, LCMethodDeclaration getter, LCMethodDeclaration setter, boolean hasSemiColon, Position pos, boolean isErrorNode) {
        this(null, isVal, name, typeExpression, extended, delegated, init, getter, setter, hasSemiColon, pos, isErrorNode);
    }

    public LCVariableDeclaration(LCModifier modifier, boolean isVal, String name, LCTypeExpression typeExpression, LCExpression delegated, LCExpression init, LCMethodDeclaration getter, LCMethodDeclaration setter, boolean hasSemiColon, Position pos, boolean isErrorNode) {
        this(modifier, isVal, name, typeExpression, null, delegated, init, getter, setter, hasSemiColon, pos, isErrorNode);
    }

    public LCVariableDeclaration(LCModifier modifier, boolean isVal, String name, LCTypeExpression typeExpression, LCTypeReferenceExpression extended, LCExpression delegated, LCExpression init, LCMethodDeclaration getter, LCMethodDeclaration setter, boolean hasSemiColon, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        if (modifier != null) this.setModifier(modifier);

        this.isVal = isVal;
        if (name != null) {
            this.name = name;
        } else {
            this.name = "<unnamed_var_" + unnamedVarCount + ">";
            unnamedVarCount++;
        }

        this.typeExpression = typeExpression;
        if (this.typeExpression != null) this.typeExpression.parentNode = this;

        this.extended = extended;
        if (this.extended != null) this.extended.parentNode = this;

        this.delegated = delegated;
        if (this.delegated != null) this.delegated.parentNode = this;

        this.init = init;
        if (this.init != null) this.init.parentNode = this;

        this.getter = getter;
        if (this.getter != null) this.getter.parentNode = this;
        this.setter = setter;
        if (this.setter != null) this.setter.parentNode = this;

        this.hasSemiColon = hasSemiColon;
    }

    public final void setModifier(LCModifier LCModifier) {
        this.modifier = LCModifier;
        this.modifier.parentNode = this;

        if (this.isVal) {
            this.modifier.flags |= LCFlags.FINAL;
        }
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitVariableDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCVariableDeclaration{" +
                "annotations=" + Arrays.toString(annotations) +
                ", modifier=" + modifier +
                ", isVal=" + isVal +
                ", name='" + name + '\'' +
                ", typeExpression=" + typeExpression +
                ", extended=" + extended +
                ", delegated=" + delegated +
                ", init=" + init +
                ", getter=" + getter +
                ", setter=" + setter +
                ", hasSemiColon=" + hasSemiColon +
                ", theType=" + theType +
                ", symbol=" + symbol +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCVariableDeclaration clone() throws CloneNotSupportedException {
        return new LCVariableDeclaration(this.modifier.clone(), this.isVal, this.name, typeExpression != null ? typeExpression.clone() : null, extended != null ? extended.clone() : null, this.delegated != null ? this.delegated.clone() : null, init != null ? init.clone() : null, this.getter != null ? getter.clone() : null, this.setter != null ? setter.clone() : null, hasSemiColon, this.position.clone(), isErrorNode);
    }
}
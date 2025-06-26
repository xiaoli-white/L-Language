package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.ClassSymbol;

import java.util.Arrays;

public final class LCClassDeclaration extends LCObjectDeclaration {
    public LCTypeReferenceExpression extended;
    public LCTypeReferenceExpression[] implementedInterfaces;
    public LCTypeReferenceExpression[] permittedClasses;
    public ClassSymbol symbol = null;
    public LCExpression delegated;

    public LCClassDeclaration(String name, LCTypeParameter[] typeParameters, LCTypeReferenceExpression extended, LCTypeReferenceExpression[] implementedInterfaces, LCTypeReferenceExpression[] permittedClasses,
                              LCExpression delegated, LCBlock body, Position pos, boolean isErrorNode) {
        super(name, typeParameters, body, pos, isErrorNode);
        this.extended = extended;
        if (this.extended != null) this.extended.parentNode = this;
        this.implementedInterfaces = implementedInterfaces;
        for (LCTypeReferenceExpression implementedInterface : this.implementedInterfaces)
            implementedInterface.parentNode = this;
        this.permittedClasses = permittedClasses;
        for (LCTypeReferenceExpression permitsClass : this.permittedClasses)
            permitsClass.parentNode = this;
        this.delegated = delegated;
        if (this.delegated != null) this.delegated.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitClassDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCClassDeclaration{" +
                "extended=" + extended +
                ", implementedInterfaces=" + Arrays.toString(implementedInterfaces) +
                ", permittedClasses=" + Arrays.toString(permittedClasses) +
                ", symbol=" + symbol +
                ", delegated=" + delegated +
                ", scope=" + scope +
                ", modifier=" + modifier +
                ", name='" + name + '\'' +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", body=" + body +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCClassDeclaration clone() throws CloneNotSupportedException {
        return new LCClassDeclaration(this.name, Arrays.copyOf(this.typeParameters, this.typeParameters.length), this.extended != null ? this.extended.clone() : null, Arrays.copyOf(this.implementedInterfaces, this.implementedInterfaces.length), Arrays.copyOf(this.permittedClasses, this.permittedClasses.length), this.delegated != null ? this.delegated.clone() : null, this.body != null ? this.body.clone() : null, this.position.clone(), this.isErrorNode);
    }
}
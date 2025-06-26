package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.InterfaceSymbol;

import java.util.Arrays;
import java.util.Objects;

public final class LCInterfaceDeclaration extends LCObjectDeclaration {
    public LCTypeReferenceExpression[] extendedInterfaces;
    public InterfaceSymbol symbol = null;
    public InterfaceSymbol[] superInterfaceSymbols = null;

    public LCInterfaceDeclaration(String name, LCTypeParameter[] typeParameters, LCTypeReferenceExpression[] extendedInterfaces, LCBlock body, Position pos, boolean isErrorNode) {
        super(name, typeParameters, body, pos, isErrorNode);
        this.extendedInterfaces = extendedInterfaces;
        for (LCTypeReferenceExpression superInterface : this.extendedInterfaces)
            superInterface.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitInterfaceDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCInterfaceDeclaration{" +
                "extendedInterfaces=" + Arrays.toString(extendedInterfaces) +
                ", symbol=" + symbol +
                ", superInterfaceSymbols=" + Arrays.toString(superInterfaceSymbols) +
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
    public LCInterfaceDeclaration clone() throws CloneNotSupportedException {
        return new LCInterfaceDeclaration(name, Arrays.copyOf(typeParameters, typeParameters.length), Arrays.copyOf(extendedInterfaces, extendedInterfaces.length), body.clone(), position.clone(), isErrorNode);
    }
}

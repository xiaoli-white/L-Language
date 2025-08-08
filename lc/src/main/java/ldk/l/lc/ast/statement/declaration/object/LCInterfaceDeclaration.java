package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.InterfaceSymbol;

import java.util.ArrayList;
import java.util.List;


public final class LCInterfaceDeclaration extends LCObjectDeclaration {
    public List<LCTypeReferenceExpression> extendedInterfaces;
    public InterfaceSymbol symbol = null;
    public List<InterfaceSymbol> superInterfaceSymbols = null;

    public LCInterfaceDeclaration(String name, List<LCTypeParameter> typeParameters, List<LCTypeReferenceExpression> extendedInterfaces, LCBlock body, Position pos, boolean isErrorNode) {
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
                "extendedInterfaces=" + extendedInterfaces +
                ", symbol=" + symbol +
                ", superInterfaceSymbols=" + superInterfaceSymbols +
                ", scope=" + scope +
                ", modifier=" + modifier +
                ", name='" + name + '\'' +
                ", typeParameters=" + typeParameters +
                ", body=" + body +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}

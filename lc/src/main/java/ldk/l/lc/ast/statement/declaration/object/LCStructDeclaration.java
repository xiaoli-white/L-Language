package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.object.StructSymbol;

import java.util.Arrays;
import java.util.Objects;

public class LCStructDeclaration extends LCObjectDeclaration {
    public StructSymbol symbol = null;

    public LCStructDeclaration(String name, LCTypeParameter[] typeParameters, LCBlock body, Position pos, boolean isErrorNode) {
        super(name, typeParameters, body, pos, isErrorNode);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitStructDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCStructDeclaration{" +
                "symbol=" + symbol +
                ", scope=" + scope +
                ", name='" + name + '\'' +
                ", modifier=" + modifier +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", body=" + body +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCStructDeclaration clone() throws CloneNotSupportedException {
        return new LCStructDeclaration(name, Arrays.copyOf(typeParameters, typeParameters.length), body.clone(), position.clone(), isErrorNode);
    }
}

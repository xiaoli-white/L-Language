package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.object.EnumSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCEnumDeclaration extends LCObjectDeclaration {
    public Scope scope;
    public EnumSymbol symbol = null;
    public List<LCTypeReferenceExpression> implementedInterfaces;
    public LCExpression delegated;
    public List<LCEnumFieldDeclaration> fields;

    public LCEnumDeclaration(String name, List<LCTypeParameter> typeParameters, List<LCTypeReferenceExpression> implementedInterfaces, LCExpression delegated,
                             List<LCEnumFieldDeclaration> fields, LCBlock body, Position pos, boolean isErrorNode) {
        super(name, typeParameters, body, pos, isErrorNode);
        this.implementedInterfaces = implementedInterfaces;
        for (LCTypeReferenceExpression implementedInterface : this.implementedInterfaces)
            implementedInterface.parentNode = this;

        this.delegated = delegated;
        if (this.delegated != null) this.delegated.parentNode = this;

        this.fields = fields;
        for (LCEnumFieldDeclaration field : this.fields) field.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitEnumDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCEnumDeclaration{" +
                "scope=" + scope +
                ", symbol=" + symbol +
                ", implementedInterfaces=" + implementedInterfaces +
                ", delegated=" + delegated +
                ", fields=" + fields +
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

    public static final class LCEnumFieldDeclaration extends LCAstNode {
        public String name;
        public List<LCExpression> arguments;
        public EnumSymbol.EnumFieldSymbol symbol = null;

        public LCEnumFieldDeclaration(String name, List<LCExpression> arguments, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.name = name;
            this.arguments = arguments;
            for (LCExpression argument : this.arguments) argument.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitEnumFieldDeclaration(this, additional);
        }

        @Override
        public String toString() {
            return "LCEnumFieldDeclaration{" +
                    "name='" + name + '\'' +
                    ", arguments=" + arguments +
                    ", symbol=" + symbol +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }
}

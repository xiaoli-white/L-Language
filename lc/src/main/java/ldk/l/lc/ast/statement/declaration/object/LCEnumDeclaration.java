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

import java.util.Arrays;
import java.util.Objects;

public class LCEnumDeclaration extends LCObjectDeclaration {
    public Scope scope;
    public EnumSymbol symbol = null;
    public LCTypeReferenceExpression[] implementedInterfaces;
    public LCExpression delegated;
    public LCEnumFieldDeclaration[] fields;

    public LCEnumDeclaration(String name, LCTypeParameter[] typeParameters, LCTypeReferenceExpression[] implementedInterfaces, LCExpression delegated,
                             LCEnumFieldDeclaration[] fields, LCBlock body, Position pos, boolean isErrorNode) {
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
                ", implementedInterfaces=" + Arrays.toString(implementedInterfaces) +
                ", delegated=" + delegated +
                ", fields=" + Arrays.toString(fields) +
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
    public LCEnumDeclaration clone() throws CloneNotSupportedException {
        return new LCEnumDeclaration(name, Arrays.copyOf(typeParameters, typeParameters.length), Arrays.copyOf(implementedInterfaces, implementedInterfaces.length), delegated != null ? delegated.clone() : null, Arrays.copyOf(fields, fields.length), body.clone(), position.clone(), isErrorNode);
    }

    public static class LCEnumFieldDeclaration extends LCAstNode {
        public String name;
        public LCExpression[] arguments;
        public EnumSymbol.EnumFieldSymbol symbol = null;

        public LCEnumFieldDeclaration(String name, LCExpression[] arguments, Position pos, boolean isErrorNode) {
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
                    ", arguments=" + Arrays.toString(arguments) +
                    ", symbol=" + symbol +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCEnumFieldDeclaration clone() throws CloneNotSupportedException {
            return new LCEnumFieldDeclaration(name, Arrays.copyOf(arguments, arguments.length), position.clone(), isErrorNode);
        }
    }
}

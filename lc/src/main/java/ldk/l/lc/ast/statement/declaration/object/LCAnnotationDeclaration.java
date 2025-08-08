package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.object.AnnotationSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCAnnotationDeclaration extends LCObjectDeclaration {
    public AnnotationSymbol symbol = null;
    public LCAnnotationBody annotationBody;

    public LCAnnotationDeclaration(String name, LCAnnotationBody annotationBody, Position position, boolean isErrorNode) {
        super(name, new ArrayList<>(), null, position, isErrorNode);
        this.annotationBody = annotationBody;
        this.annotationBody.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAnnotationDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCAnnotationDeclaration{" +
                "symbol=" + symbol +
                ", annotationBody=" + annotationBody +
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

    public static class LCAnnotationFieldDeclaration extends LCAstNode {
        public AnnotationSymbol.AnnotationFieldSymbol symbol = null;
        public String name;
        public LCTypeExpression typeExpression;
        public LCExpression defaultValue;

        public LCAnnotationFieldDeclaration(String name, LCTypeExpression typeExpression, LCExpression defaultValue, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.name = name;

            this.typeExpression = typeExpression;
            if (this.typeExpression != null) this.typeExpression.parentNode = this;

            this.defaultValue = defaultValue;
            if (this.defaultValue != null) this.defaultValue.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitAnnotationFieldDeclaration(this, additional);
        }

        @Override
        public String toString() {
            return "LCAnnotationFieldDeclaration{" +
                    "symbol=" + symbol +
                    ", name='" + name + '\'' +
                    ", typeExpression=" + typeExpression +
                    ", defaultValue=" + defaultValue +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }

    public static final class LCAnnotationBody extends LCAstNode {
        public Scope scope = null;
        public List<LCAnnotationFieldDeclaration> fields;

        public LCAnnotationBody(List<LCAnnotationFieldDeclaration> fields, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);
            this.fields = fields;
            for (LCAnnotationFieldDeclaration field : fields) field.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitAnnotationBody(this, additional);
        }

        @Override
        public String toString() {
            return "LCAnnotationBody{" +
                    "fields=" + fields +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }
    }
}

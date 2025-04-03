package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.Arrays;

public final class AnnotationSymbol extends ObjectSymbol {
    public LCAnnotationDeclaration declaration;
    public AnnotationFieldSymbol[] fields;

    public AnnotationSymbol(LCAnnotationDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, AnnotationFieldSymbol[] fields) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Annotation, templateTypeParameters, typeParameters, flags, attributes);
        this.declaration = declaration;

        this.fields = fields;
        for (AnnotationFieldSymbol field : this.fields) field.annotationSymbol = this;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitAnnotationSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "AnnotationSymbol{" +
                "fields=" + Arrays.toString(fields) +
                ", _package='" + _package + '\'' +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", templateTypeParameters=" + Arrays.toString(templateTypeParameters) +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public MethodSymbol[] getMethods() {
        return new MethodSymbol[0];
    }

    public static class AnnotationFieldSymbol extends Symbol {
        public AnnotationSymbol annotationSymbol = null;
        public LCAnnotationDeclaration.LCAnnotationFieldDeclaration decl;

        public AnnotationFieldSymbol(LCAnnotationDeclaration.LCAnnotationFieldDeclaration decl, Type theType) {
            super(decl.name, theType, SymbolKind.AnnotationField);
            this.decl = decl;
        }

        @Override
        public Object accept(SymbolVisitor visitor, Object additional) {
            return visitor.visitAnnotationFieldSymbol(this, additional);
        }

        @Override
        public String toString() {
            return "AnnotationFieldSymbol{" +
                    "name='" + name + '\'' +
                    ", theType=" + theType +
                    '}';
        }
    }
}

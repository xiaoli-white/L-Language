package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCStructDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.Arrays;

public final class StructSymbol extends ObjectSymbol {
    public LCStructDeclaration declaration;
    public VariableSymbol[] properties;
    public MethodSymbol[] constructors;
    public MethodSymbol[] methods;
    public MethodSymbol destructor;

    public StructSymbol(LCStructDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, VariableSymbol[] properties, MethodSymbol[] constructors, MethodSymbol[] methods, MethodSymbol destructor) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Struct, templateTypeParameters, typeParameters, flags, attributes);
        this.declaration = declaration;

        this.properties = properties;
        for (VariableSymbol prop : this.properties)
            prop.objectSymbol = this;

        this.constructors = constructors;
        for (MethodSymbol symbol : this.constructors)
            symbol.objectSymbol = this;

        this.methods = methods;
        for (MethodSymbol method : this.methods) {
            method.objectSymbol = this;
            if (LCFlags.hasFinal(this.flags)) method.flags |= LCFlags.FINAL;
        }

        this.destructor = destructor;
        if (this.destructor != null) {
            this.destructor.objectSymbol = this;
            if (LCFlags.hasFinal(this.flags)) this.destructor.flags |= LCFlags.FINAL;
        }
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitStructSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "StructSymbol{" +
                "properties=" + Arrays.toString(properties) +
                ", constructors=" + Arrays.toString(constructors) +
                ", methods=" + Arrays.toString(methods) +
                ", _package='" + _package + '\'' +
                ", templateTypeParameters=" + Arrays.toString(templateTypeParameters) +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public MethodSymbol[] getMethods() {
        return this.methods;
    }
}

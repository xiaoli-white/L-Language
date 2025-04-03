package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCInterfaceDeclaration;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.Arrays;
import java.util.Objects;

public final class InterfaceSymbol extends ObjectSymbol {
    public LCInterfaceDeclaration declaration;
    public MethodSymbol[] methods;
    public InterfaceSymbol[] extendedInterfaces = null;

    public InterfaceSymbol(LCInterfaceDeclaration declaration, Type theType, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes, MethodSymbol[] methods) {
        super(declaration.getPackageName(), declaration.name, theType, SymbolKind.Interface, templateTypeParameters, typeParameters, flags, attributes);
        this.declaration = declaration;

        this.methods = methods;
        for (MethodSymbol method : this.methods)
            method.objectSymbol = this;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitInterfaceSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "InterfaceSymbol{" +
                "methods=" + Arrays.toString(methods) +
                ", extendedInterfaces=" + Arrays.toString(extendedInterfaces) +
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

    public MethodSymbol getDefaultMethodCascade(String simpleName) {
        for (MethodSymbol method : this.methods) {
            if (LCFlags.hasDefault(method.flags) && Objects.equals(method.getSimpleName(), simpleName)) {
                return method;
            }
        }
        for (InterfaceSymbol interfaceSymbol : this.extendedInterfaces) {
            MethodSymbol methodSymbol = interfaceSymbol.getDefaultMethodCascade(simpleName);
            if (methodSymbol != null) return methodSymbol;
        }
        return null;
    }
}

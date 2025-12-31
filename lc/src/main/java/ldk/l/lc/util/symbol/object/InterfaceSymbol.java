package ldk.l.lc.util.symbol.object;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.statement.declaration.object.LCInterfaceDeclaration;
import ldk.l.lc.semantic.types.MethodPointerType;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class InterfaceSymbol extends ObjectSymbol {
    public LCInterfaceDeclaration declaration;
    public List<MethodSymbol> methods;
    public List<InterfaceSymbol> extendedInterfaces = null;
    public final MethodSymbol destructor;

    public InterfaceSymbol(LCInterfaceDeclaration declaration, Type theType, List<TypeParameterSymbol> typeParameters, long flags, List<String> attributes, List<MethodSymbol> methods) {
        super(declaration.getRealPackageName(), declaration.name, theType, SymbolKind.Interface, typeParameters, flags, attributes);
        this.declaration = declaration;

        this.methods = methods;
        for (MethodSymbol method : this.methods)
            method.objectSymbol = this;

        destructor = new MethodSymbol("<deinit>", List.of(), SystemTypes.VOID, new MethodPointerType(List.of(), SystemTypes.VOID), MethodKind.Method, LCFlags.PUBLIC, List.of());
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitInterfaceSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "InterfaceSymbol{" +
                "methods=" + methods +
                ", extendedInterfaces=" + extendedInterfaces +
                ", _package='" + _package + '\'' +
                ", typeParameters=" + typeParameters +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }

    @Override
    public List<MethodSymbol> getMethods() {
        return new ArrayList<>(this.methods);
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

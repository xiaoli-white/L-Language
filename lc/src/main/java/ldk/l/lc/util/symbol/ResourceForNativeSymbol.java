package ldk.l.lc.util.symbol;

import ldk.l.lc.semantic.types.Type;

public final class ResourceForNativeSymbol extends Symbol {
    public ResourceForNativeSymbol(String name, Type theType) {
        super(name, theType, SymbolKind.ResourceForNative);
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitResourceForNativeSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "ResourceForNativeSymbol{" +
                "name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }
}

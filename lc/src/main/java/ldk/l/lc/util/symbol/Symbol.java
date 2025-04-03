package ldk.l.lc.util.symbol;

import ldk.l.lc.semantic.types.Type;

public abstract class Symbol {
    public String name;
    public Type theType;
    public SymbolKind kind;

    public Symbol(String name, Type theType, SymbolKind kind) {
        this.name = name;
        this.theType = theType;
        this.kind = kind;
    }

    public abstract Object accept(SymbolVisitor visitor, Object additional);

    @Override
    public abstract String toString();
}
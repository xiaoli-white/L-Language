package ldk.l.lc.util.symbol.object;

import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

import java.util.List;

public abstract sealed class ObjectSymbol extends Symbol permits ClassSymbol, InterfaceSymbol, EnumSymbol, RecordSymbol, AnnotationSymbol {
    public String _package;
    public List<TypeParameterSymbol> typeParameters;
    public long flags;
    public List<String> attributes;

    protected ObjectSymbol(String _package, String name, Type theType, SymbolKind kind, List<TypeParameterSymbol> typeParameters, long flags, List<String> attributes) {
        super(name, theType, kind);
        this._package = _package;

        this.typeParameters = typeParameters;

        this.flags = flags;
        this.attributes = attributes;
    }

    public abstract List<MethodSymbol> getMethods();

    public String getFullName() {
        return (this._package != null && !this._package.isEmpty() ? this._package + "." : "") + this.name;
    }
}

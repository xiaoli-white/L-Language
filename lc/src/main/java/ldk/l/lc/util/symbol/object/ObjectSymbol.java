package ldk.l.lc.util.symbol.object;

import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.symbol.*;

public abstract sealed class ObjectSymbol extends Symbol permits ClassSymbol, InterfaceSymbol, EnumSymbol, RecordSymbol, StructSymbol, AnnotationSymbol{
    public String _package;
    public TemplateTypeParameterSymbol[] templateTypeParameters;
    public TypeParameterSymbol[] typeParameters;
    public long flags;
    public String[] attributes;

    public ObjectSymbol(String _package, String name, Type theType, SymbolKind kind, TemplateTypeParameterSymbol[] templateTypeParameters, TypeParameterSymbol[] typeParameters, long flags, String[] attributes) {
        super(name, theType, kind);
        this._package = _package;

        this.templateTypeParameters = templateTypeParameters;
        this.typeParameters = typeParameters;

        this.flags = flags;
        this.attributes = attributes;
    }

    public abstract MethodSymbol[] getMethods();

    public String getFullName() {
        return (this._package != null ? this._package + "." : "") + this.name;
    }
}

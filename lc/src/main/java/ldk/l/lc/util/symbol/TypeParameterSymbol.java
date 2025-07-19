package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.Arrays;
import java.util.List;

public final class TypeParameterSymbol extends Symbol {
    public LCTypeParameter declaration;
    public ObjectSymbol extended;
    public List<ObjectSymbol> implemented;
    public ObjectSymbol supered;
    public ObjectSymbol _default;

    public TypeParameterSymbol(LCTypeParameter declaration, ObjectSymbol extended, List<ObjectSymbol> implemented, ObjectSymbol supered, ObjectSymbol _default) {
        super(declaration.name, null, SymbolKind.TypeParameter);
        this.declaration = declaration;
        this.extended = extended;
        this.implemented = implemented;
        this.supered = supered;
        this._default = _default;
    }

    @Override
    public Object accept(SymbolVisitor visitor, Object additional) {
        return visitor.visitTypeParameterSymbol(this, additional);
    }

    @Override
    public String toString() {
        return "TypeParameterSymbol{" +
                "extended=" + extended +
                ", implemented=" + implemented +
                ", supered=" + supered +
                ", _default=" + _default +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }
}

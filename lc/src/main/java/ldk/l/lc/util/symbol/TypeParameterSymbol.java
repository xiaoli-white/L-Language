package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.Arrays;

public class TypeParameterSymbol extends Symbol {
    public LCTypeParameter decl;
    public ObjectSymbol extended;
    public ObjectSymbol[] implemented;
    public ObjectSymbol supered;
    public ObjectSymbol _default;

    public TypeParameterSymbol(LCTypeParameter decl, ObjectSymbol extended, ObjectSymbol[] implemented, ObjectSymbol supered, ObjectSymbol _default) {
        super(decl.name, null, SymbolKind.TypeParameter);
        this.decl = decl;
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
                ", implemented=" + Arrays.toString(implemented) +
                ", supered=" + supered +
                ", _default=" + _default +
                ", name='" + name + '\'' +
                ", theType=" + theType +
                '}';
    }
}

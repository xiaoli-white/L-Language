package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.type.LCTypeExpression;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.TypeParameterSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCTypeParameter extends LCAstNode {
    public TypeParameterSymbol symbol;
    public String name;
    public LCTypeReferenceExpression extended;
    public List<LCTypeReferenceExpression> implemented;
    public LCTypeReferenceExpression supered;
    public LCTypeReferenceExpression _default;

    public LCTypeParameter(String name, LCTypeReferenceExpression extended, List<LCTypeReferenceExpression> implemented, LCTypeReferenceExpression supered, LCTypeReferenceExpression _default, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;

        this.extended = extended;
        if (this.extended != null) this.extended.parentNode = this;

        this.implemented = implemented;
        for (LCTypeExpression type : this.implemented) type.parentNode = this;

        this.supered = supered;
        if (this.supered != null) this.supered.parentNode = this;

        this._default = _default;
        if (this._default != null) this._default.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTypeParameter(this, additional);
    }

    @Override
    public String toString() {
        return "LCTypeParameter{" +
                "name='" + name + '\'' +
                ", extended=" + extended +
                ", implemented=" + implemented +
                ", supered=" + supered +
                ", _default=" + _default +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCTypeParameter clone() throws CloneNotSupportedException {
        return new LCTypeParameter(name, extended != null ? extended.clone() : null, new ArrayList<>(implemented), supered != null ? supered.clone() : null, _default != null ? _default.clone() : null, position.clone(), isErrorNode);
    }
}

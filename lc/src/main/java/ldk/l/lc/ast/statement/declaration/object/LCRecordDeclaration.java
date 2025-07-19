package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCTypeParameter;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;
import ldk.l.lc.util.symbol.object.RecordSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCRecordDeclaration extends LCObjectDeclaration {
    public Scope scope;
    public RecordSymbol symbol = null;
    public List<LCVariableDeclaration> fields;
    public List<LCTypeReferenceExpression> implementedInterfaces;
    public LCExpression delegated;

    public LCRecordDeclaration(String name, List<LCTypeParameter> typeParameters, List<LCVariableDeclaration> fields, List<LCTypeReferenceExpression> implementedInterfaces, LCExpression delegated, LCBlock body, Position pos, boolean isErrorNode) {
        super(name, typeParameters, body, pos, isErrorNode);
        this.fields = fields;
        for (LCVariableDeclaration field : this.fields) field.parentNode = this;

        this.implementedInterfaces = implementedInterfaces;
        for (LCTypeReferenceExpression implementedInterface : this.implementedInterfaces)
            implementedInterface.parentNode = this;
        this.delegated = delegated;
        if (this.delegated != null) this.delegated.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitRecordDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "LCRecordDeclaration{" +
                "scope=" + scope +
                ", symbol=" + symbol +
                ", fields=" + fields +
                ", implementedInterfaces=" + implementedInterfaces +
                ", delegated=" + delegated +
                ", scope=" + scope +
                ", modifier=" + modifier +
                ", name='" + name + '\'' +
                ", typeParameters=" + typeParameters +
                ", body=" + body +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCRecordDeclaration clone() throws CloneNotSupportedException {
        return new LCRecordDeclaration(name, new ArrayList<>(typeParameters), new ArrayList<>(fields), new ArrayList<>(implementedInterfaces), delegated != null ? delegated.clone() : null, body.clone(), position.clone(), isErrorNode);
    }
}

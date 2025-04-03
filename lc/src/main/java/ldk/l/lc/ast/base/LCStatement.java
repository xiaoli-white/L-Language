package ldk.l.lc.ast.base;

import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.util.Position;

public abstract class LCStatement extends LCAstNode {
    public String[] labels = null;
    public LCAnnotationDeclaration.LCAnnotation[] annotations = null;

    public LCStatement(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public final void setAnnotations(LCAnnotationDeclaration.LCAnnotation[] annotations) {
        this.annotations = annotations;
        for (LCAnnotationDeclaration.LCAnnotation lcAnnotation : this.annotations) {
            lcAnnotation.parentNode = this;
        }
    }

    @Override
    public abstract LCStatement clone() throws CloneNotSupportedException;
}
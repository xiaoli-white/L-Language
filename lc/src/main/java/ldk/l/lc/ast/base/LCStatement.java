package ldk.l.lc.ast.base;

import ldk.l.lc.util.Position;

public abstract class LCStatement extends LCAstNode {
    public String[] labels = null;
    public LCAnnotation[] annotations = null;

    public LCStatement(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public final void setAnnotations(LCAnnotation[] annotations) {
        this.annotations = annotations;
        for (LCAnnotation lcAnnotation : this.annotations) {
            lcAnnotation.parentNode = this;
        }
    }

    @Override
    public abstract LCStatement clone() throws CloneNotSupportedException;
}
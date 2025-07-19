package ldk.l.lc.ast.base;

import ldk.l.lc.util.Position;

import java.util.List;

public abstract class LCStatement extends LCAstNode {
    public List<String> labels = null;
    public List<LCAnnotation> annotations = null;

    public LCStatement(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }

    public final void setAnnotations(List<LCAnnotation> annotations) {
        this.annotations = annotations;
        for (LCAnnotation lcAnnotation : this.annotations) {
            lcAnnotation.parentNode = this;
        }
    }

    @Override
    public abstract LCStatement clone() throws CloneNotSupportedException;
}
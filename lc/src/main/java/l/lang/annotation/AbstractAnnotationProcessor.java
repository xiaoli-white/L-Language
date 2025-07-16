package l.lang.annotation;

import ldk.l.lc.ast.base.LCAnnotation;

public abstract class AbstractAnnotationProcessor {
    public abstract void process(LCAnnotation annotation);
}

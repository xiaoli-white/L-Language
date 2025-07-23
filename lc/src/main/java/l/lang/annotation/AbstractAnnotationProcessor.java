package l.lang.annotation;

import ldk.l.lc.ast.base.LCAnnotation;
import ldk.l.util.option.Options;

public abstract class AbstractAnnotationProcessor {
    public abstract boolean process(LCAnnotation annotation, Options options);
}

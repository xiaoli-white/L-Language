package ldk.l.lc.semantic;

import ldk.l.lc.ast.base.LCAnnotation;
import ldk.l.lc.util.error.ErrorStream;

import java.util.List;

public final class AnnotationProcessor {
    private final ErrorStream errorStream;
    public AnnotationProcessor(ErrorStream errorStream){
        this.errorStream = errorStream;
    }
    public void process(List<LCAnnotation> annotations){
        for (LCAnnotation annotation : annotations) {
        }
    }
}
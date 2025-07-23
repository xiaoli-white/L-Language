package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAnnotation;

import java.util.ArrayList;
import java.util.List;

public final class AnnotationCollector extends LCAstVisitor {
    private final List<LCAnnotation> annotations = new ArrayList<>();

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        return super.visitAst(ast, additional);
    }

    @Override
    public Object visitAnnotation(LCAnnotation lcAnnotation, Object additional) {
        this.annotations.add(lcAnnotation);
        return null;
    }

    public List<LCAnnotation> getAnnotations() {
        List<LCAnnotation> result = new ArrayList<>(annotations);
        annotations.clear();
        return result;
    }
}

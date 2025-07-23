package l.lang.annotation;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.base.LCAnnotation;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.util.option.Options;

public abstract class AbstractAnnotationProcessor {
    public abstract boolean process(LCAnnotation annotation, Options options);

    protected final void addSourceFile(LCAst ast, LCSourceFile sourceFile) {
        ast.addSourceFile(sourceFile);
        ast.sourceFileQueue.add(sourceFile);
    }
}

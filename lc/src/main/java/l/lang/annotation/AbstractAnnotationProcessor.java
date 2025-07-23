package l.lang.annotation;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.base.LCAnnotation;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.semantic.SemanticAnalyzer;
import ldk.l.util.option.Options;

public abstract class AbstractAnnotationProcessor {
    protected final SemanticAnalyzer semanticAnalyzer;
    protected final Options options;

    protected AbstractAnnotationProcessor(SemanticAnalyzer semanticAnalyzer, Options options) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.options = options;
    }

    public abstract boolean process(LCAnnotation annotation);

    protected final void addSourceFile(LCAst ast, LCSourceFile sourceFile) {
        ast.addSourceFile(sourceFile);
        ast.sourceFileQueue.add(sourceFile);
    }
}

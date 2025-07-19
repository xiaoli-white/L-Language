package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.token.CharStream;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.util.option.Options;

public final class SemanticAnalyzer {
    private final LCAst ast;
    private final ErrorStream errorStream;
    public final TypeBuilder typeBuilder;
    public final TypeResolver typeResolver;
    public final Enter enter;
    public final TypeParameterEnter typeParameterEnter;
    public final ObjectSymbolResolver objectSymbolResolver;
    public final ReferenceResolver referenceResolver;
    public final TypeChecker typeChecker;
    public final ModifierChecker modifierChecker;
    public final ClosureAnalyzer closureAnalyzer;
    public final AssignAnalyzer assignAnalyzer;
    public final LiveAnalyzer liveAnalyzer;
    public final LeftValueAttributor leftValueAttributor;
    public final AnnotationCollector annotationCollector;
    public final AnnotationProcessor annotationProcessor;

    public SemanticAnalyzer(CharStream charStream, LCAst ast, ErrorStream errorStream, Options options) {
        this.ast = ast;
        this.ast.mainMethod = null;

        this.errorStream = errorStream;

        this.typeBuilder = new TypeBuilder();
        this.typeResolver = new TypeResolver(errorStream);
        this.enter = new Enter(errorStream);
        this.typeParameterEnter = new TypeParameterEnter(errorStream);
        this.objectSymbolResolver = new ObjectSymbolResolver(errorStream);
        this.referenceResolver = new ReferenceResolver(this, errorStream);
        this.typeChecker = new TypeChecker(charStream, this, errorStream, options);
        this.modifierChecker = new ModifierChecker(errorStream);
        this.closureAnalyzer = new ClosureAnalyzer(errorStream, options);
        this.assignAnalyzer = new AssignAnalyzer(errorStream);
        this.liveAnalyzer = new LiveAnalyzer(errorStream);
        this.leftValueAttributor = new LeftValueAttributor(errorStream);
        this.annotationCollector = new AnnotationCollector();
        this.annotationProcessor = new AnnotationProcessor(errorStream);
    }

    public void execute() {
        this.typeBuilder.visitAst(this.ast, null);
        SystemTypes.setObjectTypes(this.ast, this.errorStream);
        this.typeResolver.visitAst(this.ast, null);
        this.enter.visitAst(this.ast, null);
        this.typeParameterEnter.visitAst(this.ast, null);
        this.objectSymbolResolver.visitAst(this.ast, null);
        this.referenceResolver.visitAst(this.ast, null);
        this.typeChecker.visitAst(this.ast, null);
        this.modifierChecker.visitAst(this.ast, null);
//        this.closureAnalyzer.visitAst(this.ast, null);
//        this.assignAnalyzer.visitAst(this.ast, null);
//        this.liveAnalyzer.visitAst(this.ast, null);
        this.leftValueAttributor.visitAst(this.ast, null);
        this.annotationCollector.visitAst(this.ast, null);
        this.annotationProcessor.process(annotationCollector.getAnnotations());
    }
}
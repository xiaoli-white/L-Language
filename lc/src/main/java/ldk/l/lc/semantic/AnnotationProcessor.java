package ldk.l.lc.semantic;

import l.lang.annotation.AbstractAnnotationProcessor;
import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.base.LCAnnotation;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lpm.PackageManager;
import ldk.l.util.option.Options;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AnnotationProcessor {
    private final SemanticAnalyzer semanticAnalyzer;
    private final Options options;
    private final ErrorStream errorStream;

    public AnnotationProcessor(SemanticAnalyzer semanticAnalyzer, Options options, ErrorStream errorStream) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.options = options;
        this.errorStream = errorStream;
    }

    public void process(LCAst ast, List<LCAnnotation> annotations) {
        Map<String, List<AbstractAnnotationProcessor>> annotationProcessors = getAnnotationProcessors();
        for (LCAnnotation annotation : annotations) {
            String annotationName = annotation.symbol.getFullName();
            for (AbstractAnnotationProcessor annotationProcessor : annotationProcessors.getOrDefault(annotationName, new ArrayList<>())) {
                if (!annotationProcessor.process(annotation)) {
                    System.err.println("Annotation processor failed: " + annotationName);
                }
            }
        }
        while (!ast.sourceFileQueue.isEmpty()) {
            LCSourceFile sourceFile = ast.sourceFileQueue.poll();
            semanticAnalyzer.typeBuilder.visit(sourceFile, null);
            semanticAnalyzer.typeResolver.visit(sourceFile, null);
            semanticAnalyzer.enter.visit(sourceFile, null);
            semanticAnalyzer.objectSymbolResolver.visit(sourceFile, null);
            semanticAnalyzer.referenceResolver.visit(sourceFile, null);
            semanticAnalyzer.typeChecker.visit(sourceFile, null);
            semanticAnalyzer.modifierChecker.visit(sourceFile, null);
//            semanticAnalyzer.closureAnalyzer.visit(sourceFile, null);
//            semanticAnalyzer.assignAnalyzer.visit(sourceFile, null);
            semanticAnalyzer.liveAnalyzer.visit(sourceFile, null);
            semanticAnalyzer.leftValueAttributor.visit(sourceFile, null);
            semanticAnalyzer.annotationCollector.visit(sourceFile, null);
            semanticAnalyzer.annotationProcessor.process(ast, semanticAnalyzer.annotationCollector.getAnnotations());
        }
    }

    private Map<String, List<AbstractAnnotationProcessor>> getAnnotationProcessors() {
        PackageManager packageManager = new PackageManager();
        List<Map<String, Object>> annotationProcessors = packageManager.listPackages().values().stream().filter(map -> "lc-annotation-processor".equals(map.get("type"))).toList();
        Map<String, List<AbstractAnnotationProcessor>> annotationProcessorsMap = new HashMap<>();
        for (Map<String, Object> packageInfo : annotationProcessors) {
            String jarFilePath = Paths.get(packageManager.getPackagePath((String) packageInfo.get("name")), (String) packageInfo.get("main-jar")).toString();
            URL jarUrl;
            try {
                jarUrl = new File(jarFilePath).toURI().toURL();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, AnnotationProcessor.class.getClassLoader())) {
                Map<?, ?> processors = (Map<?, ?>) packageInfo.get("processors");
                if (processors != null) {
                    for (Object processorInfo : processors.values()) {
                        Map<?, ?> info = (Map<?, ?>) processorInfo;
                        String annotationName = (String) info.get("annotation-name");
                        annotationProcessorsMap.putIfAbsent(annotationName, new ArrayList<>());
                        Class<?> clazz = classLoader.loadClass((String) info.get("class-name"));
                        annotationProcessorsMap.get(annotationName).add((AbstractAnnotationProcessor) clazz.getDeclaredConstructor(SemanticAnalyzer.class, Options.class).newInstance(semanticAnalyzer, options));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return annotationProcessorsMap;
    }
}
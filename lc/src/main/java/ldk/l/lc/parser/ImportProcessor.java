package ldk.l.lc.parser;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.ast.file.LCSourceFileProxy;
import ldk.l.lc.ast.statement.LCImport;
import ldk.l.lc.token.CharStream;
import ldk.l.lc.token.Scanner;
import ldk.l.lc.token.Token;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.util.Util;
import ldk.l.util.Language;
import ldk.l.util.option.Options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ImportProcessor extends LCAstVisitor {
    private final Options options;
    private final ErrorStream errorStream;
    private LCAst ast = null;
    private final Map<String, List<LCSourceCodeFile>> importCache = new HashMap<>();

    public ImportProcessor(Options options, ErrorStream errorStream) {
        this.options = options;
        this.errorStream = errorStream;
    }

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        this.ast = ast;

        if (this.ast.sourceFiles.isEmpty())
            return null;

        for (LCSourceFile lcSourceFile : ast.sourceFiles) {
            if (lcSourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
                if (!importCache.containsKey(lcSourceCodeFile.packageName))
                    importCache.put(lcSourceCodeFile.packageName, new ArrayList<>());
                importCache.get(lcSourceCodeFile.packageName).add(lcSourceCodeFile);
            }
        }

        File stdDir = new File(this.options.get("rootpath", String.class) + "/l/lang");
        for (File f : Objects.requireNonNull(stdDir.listFiles())) {
            if (f.isFile() && f.getName().endsWith(".l")) this.parseFile(f);
        }

        for (LCSourceFile lcSourceFile : this.ast.sourceFiles) {
            if (lcSourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
                for (LCSourceCodeFile sourceCodeFile : importCache.get("l.lang"))
                    if (!sourceCodeFile.equals(lcSourceCodeFile) && !lcSourceCodeFile.containsProxy(lcSourceCodeFile.filepath))
                        lcSourceCodeFile.putProxy(new LCSourceFileProxy(sourceCodeFile, false));
                for (LCSourceCodeFile sourceCodeFile : importCache.get(lcSourceCodeFile.packageName)) {
                    if (!sourceCodeFile.equals(lcSourceCodeFile) && !lcSourceCodeFile.containsProxy(lcSourceCodeFile.filepath))
                        lcSourceCodeFile.putProxy(new LCSourceFileProxy(sourceCodeFile, false));
                }
                this.visit(lcSourceCodeFile, additional);
            }
        }
        for (LCSourceFile lcSourceFile : this.ast.sourceFiles) {
            if (lcSourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
                List<LCSourceFileProxy> proxies = lcSourceCodeFile.proxies.values().stream().toList();
                for (LCSourceFileProxy proxy : proxies) {
                    visit(proxy, additional);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitImport(LCImport lcImport, Object additional) {
        LCSourceCodeFile source = getEnclosingSourceCodeFile(lcImport);
        switch (lcImport.kind) {
            case Normal -> {
                if (lcImport.getName().equals("*")) {
                    for (LCSourceCodeFile sourceCodeFile : importCache.get(lcImport.getPackageName())) {
                        if (!sourceCodeFile.equals(source) && !source.containsProxy(sourceCodeFile.filepath))
                            source.putProxy(new LCSourceFileProxy(sourceCodeFile, false));
                    }
                } else {
                    for (LCSourceCodeFile sourceCodeFile : importCache.get(lcImport.getPackageName())) {
                        if (!sourceCodeFile.equals(source) && sourceCodeFile.getObjectDeclaration(lcImport.name) != null && !source.containsProxy(sourceCodeFile.filepath))
                            source.putProxy(new LCSourceFileProxy(sourceCodeFile, false));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object visitSourceFileProxy(LCSourceFileProxy lcSourceFileProxy, Object additional) {
        LCSourceCodeFile lcSourceCodeFile = getEnclosingSourceCodeFile(lcSourceFileProxy);
        if (lcSourceFileProxy.sourceFile instanceof LCSourceCodeFile source) {
            for (LCSourceFileProxy proxy : source.proxies.values()) {
                if (proxy.sourceFile instanceof LCSourceCodeFile sourceCodeFile) {
                    if (!sourceCodeFile.equals(lcSourceCodeFile) && !lcSourceCodeFile.containsProxy(sourceCodeFile.filepath)) {
                        LCSourceFileProxy p = new LCSourceFileProxy(sourceCodeFile, false);
                        lcSourceCodeFile.putProxy(p);
                        visit(p, additional);
                    }
                }
            }
        }
        return null;
    }

    private void parseFile(File file) {
        String fileLines;
        try {
            fileLines = Util.readTextFile(file.getPath());
        } catch (IOException e) {
            System.err.println("lc: read source file '" + file.getPath() + "' failed");
            return;
        }
        ErrorStream fileErrorStream = new ErrorStream(Language.zh_cn, file.getPath(), fileLines.split("\n"));

        CharStream charStream = new CharStream(fileLines);
        Scanner scanner = new Scanner(charStream, fileErrorStream);
        Token[] tokens = scanner.scan();
        Parser parser = new Parser(this.ast, this.options, tokens, fileErrorStream);
        LCSourceCodeFile lcSourceCodeFile = parser.parseAST(file);

        if (!importCache.containsKey(lcSourceCodeFile.packageName))
            importCache.put(lcSourceCodeFile.packageName, new ArrayList<>());
        importCache.get(lcSourceCodeFile.packageName).add(lcSourceCodeFile);
    }
}
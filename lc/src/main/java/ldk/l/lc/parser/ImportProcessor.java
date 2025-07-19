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
import java.util.ArrayList;

public final class ImportProcessor extends LCAstVisitor {
    private final Options options;
    private final ErrorStream errorStream;
    private LCAst ast = null;

    public ImportProcessor(Options options, ErrorStream errorStream) {
        this.options = options;
        this.errorStream = errorStream;
    }

    @Override
    public Object visitAst(LCAst ast, Object additional) {
        this.ast = ast;

        if (this.ast.sourceFiles.isEmpty())
            return null;

        this.visitImport(new LCImport(LCImport.LCImportKind.Normal, "l.lang.*", Position.origin, false), additional);
        Path firstFilepathParent = Paths.get(this.ast.sourceFiles.getFirst().filepath).getParent();
//        this.visitImportStatement(new LCImport(Paths.get(this.options.rootpath).relativize(firstFilepathParent != null ? firstFilepathParent : Paths.get("")).getParent().toString(), "*", Position.origin, false), additional);

        LCSourceFile[] lcSourceFiles = this.ast.sourceFiles.toArray(new LCSourceFile[0]);
        int lastLength = 0;
        do {
            for (int i = lastLength; i < lcSourceFiles.length; i++) {
                if (lcSourceFiles[i] instanceof LCSourceCodeFile lcSourceCodeFile) {
                    for (LCImport lcImport : lcSourceCodeFile.getImportStatements()) {
                        this.visitImport(lcImport, additional);
                    }
                }
            }
            lastLength = lcSourceFiles.length;
            lcSourceFiles = this.ast.sourceFiles.toArray(new LCSourceFile[0]);
        } while (lcSourceFiles.length > lastLength);

        for (LCSourceFile lcSourceFile : this.ast.sourceFiles) {
            if (lcSourceFile instanceof LCSourceCodeFile lcSourceCodeFile) {
                ArrayList<LCSourceFileProxy> proxies = new ArrayList<>();
                for (LCImport lcImport : lcSourceCodeFile.getImportStatements()) {
                    proxies.addAll(this.visitImport(lcImport, additional));
                }
                for (LCSourceFile source : this.ast.getSourceFileByParent(this.options.get("rootpath",String.class) + "/l/lang")) {
                    LCSourceFileProxy proxy = new LCSourceFileProxy(source, false);
                    if (!source.equals(lcSourceCodeFile) && !proxies.contains(proxy))
                        proxies.add(proxy);
                }
                for (LCSourceFile source : this.ast.getSourceFileByParent(new File(lcSourceCodeFile.filepath).getParent())) {
                    LCSourceFileProxy proxy = new LCSourceFileProxy(source, false);
                    if (!source.equals(lcSourceCodeFile) && !proxies.contains(proxy))
                        proxies.add(proxy);
                }
                lcSourceCodeFile.proxies.addAll(proxies);
            }
        }

        return null;
    }

    @Override
    public ArrayList<LCSourceFileProxy> visitImport(LCImport lcImport, Object additional) {
        ArrayList<LCSourceFileProxy> proxies = new ArrayList<>();
        switch (lcImport.kind) {
            case Normal -> {
                if (lcImport.getName().equals("*")) {
                    String path = this.options.get("rootpath",String.class) + "/" + lcImport.getPackageName().replaceAll("\\.", "/");
                    File f = new File(path);
                    if (f.isDirectory()) {
                        File[] files = f.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile()) {
                                    String filepath = file.getPath();
                                    if (!this.ast.containsSourceFile(filepath)) {
                                        if (filepath.endsWith(".l")) {
                                            if (!this.ast.containsSourceFile(filepath)) {
                                                this.parseFile(file);
                                            }
                                            proxies.add(new LCSourceFileProxy(this.ast.getSourceFile(filepath), false));
                                        }
                                    }
                                }
                            }
                        } else {
                            // TODO dump error
                        }
                    } else {
                        // TODO dump error
                    }
                } else {
                    File f = new File(this.options.get("rootpath",String.class) + "/" + lcImport.name.replaceAll("\\.", "/") + ".l");
                    if (!this.ast.containsSourceFile(f.getPath())) {
                        this.parseFile(f);
                    }
                    proxies.add(new LCSourceFileProxy(this.ast.getSourceFile(f.getPath()), false));
                }
            }
        }
        return proxies;
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
        parser.parseAST(file);

        this.visitImport(new LCImport(LCImport.LCImportKind.Normal, file.getParent() + ".*", Position.origin, false), null);
    }
}
package ldk.l.lc;

import com.xiaoli.bcg.ByteCodeGenerator;
import com.xiaoli.llvmir_generator.LLVMIRGenerator;
import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstDumper;
import ldk.l.lc.ir.IRGenerator;
import ldk.l.lc.parser.ImportProcessor;
import ldk.l.lc.parser.Parser;
import ldk.l.lc.parser.SyntaxChecker;
import ldk.l.lc.semantic.SemanticAnalyzer;
import ldk.l.lc.token.CharStream;
import ldk.l.lc.token.Scanner;
import ldk.l.lc.token.Token;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.scope.ScopeDumper;
import ldk.l.lg.ir.IRDumper;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.base.IRGlobalDataSection;
import ldk.l.lg.ir.instruction.IRInstruction;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.util.Util;
import ldk.l.util.Language;
import ldk.l.util.option.Options;
import ldk.l.util.option.OptionsParser;
import ldk.l.util.option.Type;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LCompiler {
    public static void main(String[] args) {
        LCompiler.parse(LCompiler.getOptionsParser().parse(args));
    }

    public static void parse(Options options) {
        String sourceFile = options.args().getFirst();
        boolean verbose = options.get("verbose",Boolean.class);
        if (sourceFile != null) {
            if (!sourceFile.endsWith(".l")) {
                // TODO dump error
                return;
            }
            String fileLines;
            try {
                fileLines = Util.readTextFile(sourceFile);
            } catch (IOException e) {
                System.err.println("lc: read source file '" + sourceFile + "' failed");
                return;
            }
            ErrorStream errorStream = new ErrorStream(Language.zh_cn, sourceFile, fileLines.split("\n"));


            CharStream charStream = new CharStream(fileLines);
            Scanner scanner = new Scanner(charStream, errorStream);
            if (verbose)
                System.out.println("词法分析中...");
            Token[] tokens = scanner.scan();
            if (verbose) {
                for (Token token : tokens) {
                    System.out.printf("text: '%s', kind: %s, code: %s\n", token.text().replace("\n", "\\n"), token.kind(), token.code());
                }
            }
            if (!errorStream.checkErrorNum(""))
                return;


            if (verbose)
                System.out.println("语法分析中...");
            LCAst ast = new LCAst();
            Parser parser = new Parser(ast, options, tokens, errorStream);
            parser.parseAST(new File(sourceFile));

            if (verbose)
                System.out.println("处理ImportStatement...");
            ImportProcessor importProcessor = new ImportProcessor(options, errorStream);
            importProcessor.visitAst(ast, null);

            LCAstDumper astDumper = new LCAstDumper();
            if (verbose) {
                astDumper.visitAst(ast, "");
            }
            if (!errorStream.checkErrorNum(""))
                return;

            if (verbose)
                System.out.println("语法检查...");
            SyntaxChecker syntaxChecker = new SyntaxChecker(errorStream);
            syntaxChecker.visitAst(ast, null);
            if (!errorStream.checkErrorNum(""))
                return;


            if (verbose)
                System.out.println("语义分析中...");
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(charStream, ast, errorStream, options);
            semanticAnalyzer.execute();
            ScopeDumper scopeDumper = new ScopeDumper();
            if (verbose) {
                astDumper.visitAst(ast, "");
                scopeDumper.visitAst(ast, "");
            }
            if (!errorStream.checkErrorNum(""))
                return;


            if (verbose)
                System.out.println("IR生成中...");
            IRModule irModule = new IRModule();
            IRGenerator irGenerator = new IRGenerator(irModule, options, errorStream);
            irGenerator.visitAst(ast, null);

            if (verbose) {
                System.out.println("Entry point: " + irModule.entryPoint);
                for (IRStructure structure : irModule.structures.values()) {
                    System.out.println("structure " + structure.name);
                    for (IRField field : structure.fields) {
                        System.out.println("\t" + field.name + ", " + field.type);
                    }
                }
                System.out.println("constant pool:");
                for (int i = 0; i < irModule.constantPool.entries.size(); i++) {
                    System.out.println("\t" + i + " => " + irModule.constantPool.entries.get(i));
                }
                System.out.println("Global data section:");
                for (IRGlobalDataSection.GlobalData globalData : irModule.globalDataSection.dataList) {
                    System.out.println("\t" + globalData);
                }
                System.out.println("Global init section:");
                for (IRControlFlowGraph.BasicBlock basicBlock : irModule.globalInitSection.basicBlocks.values()) {
                    System.out.printf("\t#%s:\n", basicBlock.name);
                    for (IRInstruction instruction : basicBlock.instructions) {
                        System.out.println("\t\t" + instruction);
                    }
                }
                for (IRFunction function : irModule.functions.values()) {
                    System.out.println("function " + function.returnType + " " + function.name + ", arguments count: " + function.argumentCount);
                    for (IRField field : function.fields) {
                        System.out.println("\t" + field.name + ", " + field.type);
                    }
                    for (IRControlFlowGraph.BasicBlock basicBlock : function.controlFlowGraph.basicBlocks.values()) {
                        System.out.printf("\t#%s:\n", basicBlock.name);
                        for (IRInstruction instruction : basicBlock.instructions) {
                            System.out.println("\t\t" + instruction);
                        }
                    }
                    System.out.println("\toutEdges:");
                    for (Map.Entry<IRControlFlowGraph.BasicBlock, List<IRControlFlowGraph.BasicBlock>> entry : function.controlFlowGraph.outEdges.entrySet()) {
                        System.out.printf("\t\t#%s=>\n", entry.getKey().name);
                        entry.getValue().forEach(basicBlock -> System.out.printf("\t\t\t#%s\n", basicBlock.name));
                    }
                    System.out.println("\tinEdges:");
                    for (Map.Entry<IRControlFlowGraph.BasicBlock, List<IRControlFlowGraph.BasicBlock>> entry : function.controlFlowGraph.inEdges.entrySet()) {
                        System.out.printf("\t\t#%s<=\n", entry.getKey().name);
                        entry.getValue().forEach(basicBlock -> System.out.printf("\t\t\t#%s\n", basicBlock.name));
                    }
                }
            }

            IRDumper irDumper = new IRDumper();
            if (verbose)
                irDumper.visitModule(irModule, "");
            if (!errorStream.checkErrorNum(""))
                return;

            ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator();
            byteCodeGenerator.generate(irModule, options);

//            LLVMIRGenerator llvmIRGenerator = new LLVMIRGenerator();
//            llvmIRGenerator.generate(irModule, options);

            errorStream.dumpErrorsAndWarnings("");
        }
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .add(List.of("--help", "-h"), "help", Type.Boolean, false)
                .add(List.of("--version", "-v"), "version", Type.Boolean, false)
                .add(List.of("--verbose", "-verbose"), "verbose", Type.Boolean, false)
                .add(List.of("--traceTypeChecker"), "traceTypeChecker", Type.Boolean, false)
                .add(List.of("--rootpath"), "rootpath", Type.String, ".")
                .add(List.of("--platform"), "platform", Type.String, "lvm")
                .add(List.of("--output", "-o"), "output", Type.String, "");
    }
}

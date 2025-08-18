package ldk.l.litec

import ldk.l.litec.parser.Lexer
import ldk.l.litec.util.Stdin
import ldk.l.litec.util.Stdout

/**
 * LiteC 编译器主入口，包含REPL（读取-解析-打印循环）功能
 */
object LiteCompiler {
    // REPL模式的提示符
    private const val PROMPT = "> "

    // 退出命令集合
    private val EXIT_COMMANDS = setOf(":q", "exit", "quit", ":exit")

    /**
     * 启动REPL交互模式
     */
    fun startRepl() {
        // 初始化输入输出工具
        Stdout.initialize()
        printWelcomeMessage()

        // 启动交互循环
        val replLoop = generateSequence { readInput() }
        replLoop.takeWhile { processInput(it) }.count()

        // 退出时清理资源
        shutdown()
    }

    /**
     * 打印欢迎信息
     */
    private fun printWelcomeMessage() {
        Stdout.println("========================================")
        Stdout.println("LiteC 交互式解释器 v1.0")
        Stdout.println("支持中文标识符和基本语法分析")
        Stdout.println("输入表达式或命令，输入 ${EXIT_COMMANDS.joinToString("/")} 退出")
        Stdout.println("输入 :help 查看帮助")
        Stdout.println("========================================")
    }

    /**
     * 读取用户输入
     */
    private fun readInput(): String? {
        try {
            Stdout.print(PROMPT)
            Stdout.flush()
            return Stdin.readLine()?.trim()
        } catch (e: Exception) {
            Stdout.printlnErr("输入错误: ${e.message ?: "未知错误"}")
            return null
        }
    }

    /**
     * 处理用户输入（核心逻辑）
     * @return 是否继续循环（false表示退出）
     */
    private fun processInput(input: String?): Boolean {
        if (input.isNullOrEmpty()) return true

        // 处理退出命令
        if (input in EXIT_COMMANDS) {
            Stdout.println("感谢使用 LiteC，再见！")
            return false
        }

        // 处理帮助命令
        if (input == ":help") {
            printHelp()
            return true
        }

        // 处理语法分析命令（显示Token）
        if (input == ":tokens") {
            Stdout.println("请输入需要分析的表达式:")
            val expr = readInput() ?: return true
            analyzeTokens(expr)
            return true
        }

        // 默认处理：执行完整编译流程（词法分析 -> 语法分析 -> 解释执行）
        compileAndExecute(input)
        return true
    }

    /**
     * 打印帮助信息
     */
    private fun printHelp() {
        Stdout.println("可用命令:")
        Stdout.println("  <表达式>       - 执行表达式并输出结果")
        Stdout.println("  :tokens        - 显示表达式的词法分析结果（Token列表）")
        Stdout.println("  :help          - 显示帮助信息")
        Stdout.println("  ${EXIT_COMMANDS.joinToString("/")} - 退出解释器")
    }

    /**
     * 分析表达式的Token
     */
    private fun analyzeTokens(expr: String) {
        if (expr.isBlank()) {
            Stdout.printlnErr("错误: 表达式不能为空")
            return
        }

        try {
            Lexer.resetErrors()
            val tokens = Lexer.tokenize(expr, "<REPL>")

            if (Lexer.hadError()) {
                Stdout.printlnErr("词法分析错误:")
                Lexer.printErrors()
            } else {
                Stdout.println("词法分析成功，共 ${tokens.size} 个Token:")
                Lexer.printTokens(tokens)
            }
        } catch (e: Exception) {
            Stdout.printlnErr("分析Token时出错: ${e.message}")
        }
    }

    /**
     * 执行完整编译流程（当前仅包含词法分析，可扩展）
     */
    private fun compileAndExecute(input: String) {
        try {
            // 1. 词法分析
            Lexer.resetErrors()
            val tokens = Lexer.tokenize(input, "<REPL>")

            if (Lexer.hadError()) {
                Lexer.printErrors()
                return
            }

            // 2. 语法分析（此处可扩展添加语法分析逻辑）
            Stdout.println("语法分析成功")

            // 3. 解释执行（此处可扩展添加执行逻辑）
            Stdout.println("并未实现运行抱歉")

        } catch (e: Exception) {
            Stdout.printlnErr("编译执行错误: ${e.message ?: e.toString()}")
        }
    }

    /**
     * 关闭资源
     */
    private fun shutdown() {
        Stdin.close()
    }
}

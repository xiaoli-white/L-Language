package ldk.l.litec.parser

// 补充必要地导入语句
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.EmptyParser
import com.github.h0tk3y.betterParse.parser.Parser
import ldk.l.litec.util.Position
import ldk.l.litec.util.Stdout
import ldk.l.litec.util.error.CompilerError
import ldk.l.litec.util.error.ErrorFormatter
import ldk.l.litec.util.error.ErrorType

object Lexer : Grammar<Unit>() {
    // 1. 实现 rootParser（使用库提供的空解析器）
    override val rootParser: Parser<Unit> = EmptyParser

    // 2. 定义原生 Token 规则（按优先级排序：特殊/短 Token 放前面）
    // 关键字（优先级最高，避免被标识符匹配）
    val letToken by literalToken("let")
    val ifToken by literalToken("if")
    val elseToken by literalToken("else")
    val funToken by literalToken("fun")

    // 运算符和分隔符
    val plusToken by literalToken("+")
    val minusToken by literalToken("-")
    val starToken by literalToken("*")
    val slashToken by literalToken("/")
    val eqToken by literalToken("==")
    val assignToken by literalToken("=")
    val lParenToken by literalToken("(")
    val rParenToken by literalToken(")")
    val lBraceToken by literalToken("{")
    val rBraceToken by literalToken("}")
    val semicolonToken by literalToken(";")
    val commaToken by literalToken(",")

    // 字面量和标识符（优先级最低）
    val numberToken by regexToken("\\d+")                 // 整数
    val stringToken by regexToken("\".*?\"")              // 字符串（简化版）
    val identifierToken by regexToken("[\\p{L}_][\\p{L}\\p{N}_]*")    // 标识符（变量名/函数名）

    // 忽略空白字符（空格、换行、制表符等）
    val whitespace by regexToken("\\s+", ignore = true)

    // 合法字符集（用于检查非法字符）
    private val legalChars = buildSet<Char> {
        // ASCII可打印字符（32-126）
        addAll(32.toChar()..126.toChar())
        // 允许的控制字符
        add('\t')  // 制表符
        add('\n')  // 换行符
        add('\r')  // 回车符
    }

    // 存储发现的错误
    val errors = mutableListOf<CompilerError.LexerError>()

    // 重置错误列表（在每次新的词法分析前调用）
    fun resetErrors() {
        errors.clear()
    }

    fun hadError(): Boolean = errors.isNotEmpty()

    // 3. 词法分析核心方法：将源代码转换为原生 Token 序列（包含位置信息）
    fun tokenize(source: String, filePath: String): List<TokenMatch> {
        resetErrors() // 每次分析前清空错误列表
        checkForIllegalCharacters(source, filePath) // 检查非法字符
        return tokenizer.tokenize(source).toList() // 生成并返回 Token 序列
    }

    private fun checkForIllegalCharacters(source: String, filePath: String?) {
        var line = 1
        var column = 1

        for (char in source) {
            // 修复：移除字符串"=="，仅保留单个字符
            val isLegal = when {
                char.isLetter() || char.isDigit() -> true // 支持Unicode字母/数字
                char in setOf('+', '-', '*', '/', '=', ';', '(', ')', '{', '}', ',', '"', '_') -> true
                char.isWhitespace() -> true
                else -> false
            }

            if (!isLegal) {
                errors.add(
                    CompilerError.LexerError(
                        position = Position(line, column),
                        source = source,
                        errorType = ErrorType.ILLEGAL_CHARACTER,
                        filePath = filePath,
                        templateArgs = arrayOf(char),
                        note = "不支持的字符 `$char`（支持中文、字母、数字、下划线及指定运算符）", // 修正提示
                        help = "请替换为合法字符"
                    )
                )
            }

            // 修复：行列号计算
            when (char) {
                '\n' -> { line++; column = 1 }
                '\t' -> column += 4
                '\r' -> {}
                else -> column++
            }
        }
    }

    // 计算行号和列号
    private fun calculatePosition(offset: Int, source: CharSequence): Pair<Int, Int> {
        if (offset <= 0) return 1 to 1
        val substring = source.take(offset)
        val line = substring.count { it == '\n' } + 1
        val lastNewlineIndex = substring.lastIndexOf('\n').takeIf { it != -1 } ?: -1
        val column = if (lastNewlineIndex == -1) offset else (offset - lastNewlineIndex)
        return line to column
    }

    fun printErrors() {
        if (errors.isEmpty()) {
            Stdout.println("未发现词法错误")
            return
        }

        Stdout.println("发现 ${errors.size} 个词法错误:")
        errors.forEach {
            Stdout.println(ErrorFormatter.format(it))
        }
    }

    fun printTokens(tokens: List<TokenMatch>) {
        tokens.forEach { tokenMatch ->
            val startOffset = tokenMatch.offset
            val (startLine, startColumn) = calculatePosition(startOffset, tokenMatch.input)

            Stdout.println(
                "Token: ${tokenMatch.tokenIndex} " +
                        "| Text: '${tokenMatch.text}' " +
                        "| 位置: $startLine:$startColumn " +
                        "| 偏移量: $startOffset"
            )
        }
    }
}
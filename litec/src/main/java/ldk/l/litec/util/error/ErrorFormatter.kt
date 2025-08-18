package ldk.l.litec.util.error

object ErrorFormatter {
    // ANSI 颜色转义序列
    private const val RESET = "\u001B[0m"
    private const val RED = "\u001B[31m"
    private const val YELLOW = "\u001B[33m"
    private const val BLUE = "\u001B[34m"

    // 上下文行数
    private const val CONTEXT_LINES = 2

    fun generate(errorType: ErrorType, vararg args: Any): String = errorType.template.format(*args)

    fun format(error: CompilerError): String {
        val sb = StringBuilder()

        // 1. 错误头部
        val displayPath = error.filePath ?: "<REPL>"
        val location = "$displayPath:${error.position.line}:${error.position.column}"
        sb.appendLine("${RED}error[${error.errorType.code}]: ${error.errorType.message}$RESET")
        sb.appendLine("${BLUE}in the $location$RESET")

        // 2. 源代码上下文
        val sourceLines = error.source.split('\n')
        val errorLineIdx = error.position.line - 1
        val startLine = maxOf(0, errorLineIdx - CONTEXT_LINES)
        val endLine = minOf(sourceLines.lastIndex, errorLineIdx + CONTEXT_LINES)

        // 计算行号宽度
        val lineNumWidth = (endLine + 1).toString().length

        for (i in startLine..endLine) {
            // 行前缀和行号
            val linePrefix = if (i == errorLineIdx) " --> " else "    "
            val lineNumber = (i + 1).toString().padStart(lineNumWidth)
            val lineContent = sourceLines[i]
            sb.appendLine("$linePrefix$lineNumber | $lineContent")

            // 错误指针（精确对齐计算）
            if (i == errorLineIdx) {
                val spaceCount = 5 + lineNumWidth + 3 + (error.position.column - 1)
                val pointer = "${" ".repeat(spaceCount)}${RED}^$RESET"
                sb.appendLine(pointer)
            }
        }

        // 3. 错误详情（移除多余的"   |"分隔线）
        val detail = generate(error.errorType, *error.templateArgs)
        sb.appendLine("   = $detail")

        // 4. 辅助提示
        error.note?.let {
            sb.appendLine("   = ${BLUE}note: $it$RESET")
        }

        // 5. 修复建议
        error.help?.let {
            sb.appendLine("   = ${YELLOW}help: $it$RESET")
        }

        return sb.toString()
    }

    fun formatWithoutColor(error: CompilerError): String {
        return format(error)
            .replace(RESET, "")
            .replace(RED, "")
            .replace(YELLOW, "")
            .replace(BLUE, "")
    }
}

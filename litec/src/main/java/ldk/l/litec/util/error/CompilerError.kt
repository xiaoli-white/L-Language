package ldk.l.litec.util.error

import ldk.l.litec.util.Position

sealed class CompilerError(
    open val position: Position,
    open val source: String,
    open val errorType: ErrorType,
    open val filePath: String? = null,
    open val templateArgs: Array<out Any>,
    open val note: String? = null,       // 辅助说明
    open val help: String? = null        // 修复建议
) {
    // 词法错误：添加templateArgs、note、help的支持
    data class LexerError(
        override val position: Position,
        override val source: String,
        override val errorType: ErrorType,
        override val filePath: String? = null,
        override val templateArgs: Array<out Any> = emptyArray(),
        override val note: String? = null,
        override val help: String? = null
    ) : CompilerError(
        position = position,
        source = source,
        errorType = errorType,
        filePath = filePath,
        templateArgs = templateArgs,
        note = note,
        help = help
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as LexerError

            if (position != other.position) return false
            if (source != other.source) return false
            if (errorType != other.errorType) return false
            if (filePath != other.filePath) return false
            if (!templateArgs.contentEquals(other.templateArgs)) return false
            if (note != other.note) return false
            if (help != other.help) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + position.hashCode()
            result = 31 * result + source.hashCode()
            result = 31 * result + errorType.hashCode()
            result = 31 * result + (filePath?.hashCode() ?: 0)
            result = 31 * result + templateArgs.contentHashCode()
            result = 31 * result + (note?.hashCode() ?: 0)
            result = 31 * result + (help?.hashCode() ?: 0)
            return result
        }
    }

    // 语法错误：在原有基础上添加templateArgs、note、help
    data class ParserError(
        override val position: Position,
        override val source: String,
        override val errorType: ErrorType,
        override val filePath: String? = null,
        val expected: String,
        val found: String,
        override val templateArgs: Array<out Any> = emptyArray(),
        override val note: String? = null,
        override val help: String? = null
    ) : CompilerError(
        position = position,
        source = source,
        errorType = errorType,
        filePath = filePath,
        templateArgs = templateArgs,
        note = note,
        help = help
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as ParserError

            if (position != other.position) return false
            if (source != other.source) return false
            if (errorType != other.errorType) return false
            if (filePath != other.filePath) return false
            if (expected != other.expected) return false
            if (found != other.found) return false
            if (!templateArgs.contentEquals(other.templateArgs)) return false
            if (note != other.note) return false
            if (help != other.help) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + position.hashCode()
            result = 31 * result + source.hashCode()
            result = 31 * result + errorType.hashCode()
            result = 31 * result + (filePath?.hashCode() ?: 0)
            result = 31 * result + expected.hashCode()
            result = 31 * result + found.hashCode()
            result = 31 * result + templateArgs.contentHashCode()
            result = 31 * result + (note?.hashCode() ?: 0)
            result = 31 * result + (help?.hashCode() ?: 0)
            return result
        }
    }
}
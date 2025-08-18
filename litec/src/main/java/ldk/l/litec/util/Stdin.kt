package ldk.l.litec.util

import java.io.BufferedInputStream
import java.io.Closeable
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object Stdin : Closeable {
    private val inputStream = BufferedInputStream(System.`in`)

    fun readLine(): String? {
        // 1. 读取原始字节并打印（关键：可视化原始字节，确认输入是否正确）
        val rawBytes = readRawBytesUntilNewline() ?: return null

        // 2. 强制尝试所有可能的编码（不依赖环境判断）
        val utf8Str = decodeWithFallback(rawBytes, StandardCharsets.UTF_8)
        val gbkStr = decodeWithFallback(rawBytes, Charset.forName("GBK"))

        // 3. 选择有效字符更多的结果（有效字符：非�和?）
        val validUtf8 = utf8Str.count { it.code != 65533 && it.code != 63 }
        val validGbk = gbkStr.count { it.code != 65533 && it.code != 63 }

        val result = if (validGbk >= validUtf8) gbkStr else utf8Str

        return result
    }

    // 读取原始字节（确保完整）
    private fun readRawBytesUntilNewline(): ByteArray? {
        val buffer = mutableListOf<Byte>()
        var byte: Int
        try {
            while (true) {
                byte = inputStream.read()
                when {
                    byte == -1 -> return if (buffer.isNotEmpty()) buffer.toByteArray() else null
                    byte == '\n'.code -> break
                    byte != '\r'.code -> buffer.add(byte.toByte())
                }
            }
        } catch (e: Exception) {
            Stdout.printlnErr("Stdin读取错误：${e.message}")
            return if (buffer.isNotEmpty()) buffer.toByteArray() else null
        }
        return buffer.toByteArray()
    }

    // 解码并处理异常
    private fun decodeWithFallback(bytes: ByteArray, charset: Charset): String {
        return try {
            String(bytes, charset)
        } catch (e: Exception) {
            String(bytes, StandardCharsets.UTF_8) // 失败时用UTF-8兜底
        }
    }

    override fun close() {
        try {
            inputStream.close()
        } catch (e: Exception) {
            Stdout.printlnErr("Stdin关闭错误：${e.message}")
        }
    }
}

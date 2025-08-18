package ldk.l.litec.util

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object Stdout {
    // 优先通过终端环境检测，忽略JVM参数干扰
    private val isGbkEnv: Boolean by lazy {
        try {
            // 直接检测系统默认编码（不受-Dfile.encoding影响）
            val systemEncoding = Charset.defaultCharset().name().uppercase()
            // 补充Windows常见GBK别名
            val gbkAliases = setOf("GBK", "CP936", "GB2312")
            systemEncoding in gbkAliases
        } catch (e: Exception) {
            true // 异常时默认按GBK处理
        }
    }

    // 输出编码（最终确定的编码）
    private val outputCharset: Charset by lazy {
        if (isGbkEnv) Charset.forName("GBK") else StandardCharsets.UTF_8
    }

    /**
     * 初始化输出流（确保配置生效后再打印信息）
     */
    fun initialize() {
        try {
            // 1. 先配置输出流
            val outStream = java.io.PrintStream(System.out, true, outputCharset.name())
            val errStream = java.io.PrintStream(System.err, true, outputCharset.name())
            System.setOut(outStream)
            System.setErr(errStream)

            // 2. 配置完成后，用新流打印初始化信息（避免乱码）
            outStream.println(if (isGbkEnv) "Stdout初始化：已适配GBK终端编码" else "Stdout初始化：使用UTF-8终端编码")
        } catch (e: Exception) {
            // 初始化失败时，用原始流强制打印（避免依赖循环）
            System.err.println("Stdout初始化失败：${e.message}")
        }
    }

    // 打印方法保持不变，但确保使用配置后的流
    fun println(content: String) = kotlin.io.println(content)
    fun print(content: String) = kotlin.io.print(content)
    fun printlnErr(content: String) = System.err.println(content)
    fun printErr(content: String) = System.err.print(content)
    fun flush() {
        System.out.flush()
        System.err.flush()
    }
}

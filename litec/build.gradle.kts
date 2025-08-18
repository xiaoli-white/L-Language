import org.gradle.jvm.toolchain.JavaLanguageVersion

// 1. 在文件顶部添加这行导入（关键！）
import java.time.Duration

// 正确的 plugins 配置（第7行问题修复）
plugins {
    java
    kotlin("jvm") version "2.2.0"
    // kotlin("plugin.java") version "2.2.0"
    application
}

group = "com.xiaoli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // 本地模块依赖
    implementation(project(":lutil"))
    implementation(project(":lg"))

    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")

    // 2. 添加 JUnit 测试依赖（解决 @Test 等注解未找到问题）
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

// 配置 JVM 工具链（Kotlin 1.9.20 推荐 JDK 17+，JDK 23 为预览版需谨慎）
java {
    toolchain { // 正确位置：java 块内
        languageVersion.set(JavaLanguageVersion.of(23))
    }

}

// 配置 jar 任务，处理重复文件
tasks.jar {
    // 策略1：排除重复的 module-info.class（推荐，不影响功能）
    exclude("META-INF/versions/9/module-info.class")

    // 策略2：若需保留，可设置为覆盖（不推荐，可能导致模块信息异常）
    // duplicatesStrategy = DuplicatesStrategy.OVERWRITE

    // 策略3：忽略重复（仅记录警告，不处理）
    // duplicatesStrategy = DuplicatesStrategy.IGNORE
}

// 应用程序配置（如需生成可执行 Jar 或启动脚本）
application {
    mainClass.set("ldk.l.litec.MainKt")
}

// 3. 测试任务配置（第61行，此时 java.time.Duration 已能识别）
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED")
        showStandardStreams = true
    }
    // 导入后，java.time.Duration 不再报错
    timeout.set(Duration.ofSeconds(300))
    maxParallelForks = if (project.hasProperty("ci")) 4 else 2
}

// 打包配置（生成可执行 Jar）
tasks.jar {
    manifest {
        attributes["Main-Class"] = "ldk.l.litec.MainKt" // 主类入口
    }
    // 合并依赖到 Jar 中（可选，适合单机运行）
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
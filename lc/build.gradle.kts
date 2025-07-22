plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.xiaoli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lutil"))
    implementation(project(":lg"))
    implementation(project(":lvm-bytecode-generator"))
    implementation(project(":llvm-ir-generator"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "ldk.l.lc.LCompiler"
    }
    mergeServiceFiles()
}

tasks.test {
    useJUnitPlatform()
}
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
    implementation("cn.hutool:hutool-all:5.8.24")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("commons-io:commons-io:2.15.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "ldk.l.lpm.LPackageManager"
    }
    mergeServiceFiles()
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
}

taboolib {
    install("common")
    install("common-5")
    install("platform-velocity")
    install("expansion-command-helper")
    classifier = null
    version = "6.0.10-114"
}

repositories {
    maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/") }
    mavenCentral()
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:1.1.8")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}
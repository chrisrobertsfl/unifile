/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.1.1/userguide/building_java_projects.html
 */

val kotestVersion = "5.7.2"
val slf4jVersion = "2.0.7"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    `java-library`
    idea
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
}

application {
    mainClass.set("com.ingenifi.unifile.MainKt")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    // Testing
    testImplementation("io.kotest:kotest-runner-junit5:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}") // replace with the latest version
    testImplementation("io.kotest:kotest-property:${kotestVersion}") // replace with the latest version
    testImplementation("io.mockk:mockk:1.13.8")

    // Utilities
    testImplementation("org.apache.commons:commons-lang3:3.13.0")
    testImplementation("com.google.guava:guava:33.0.0-jre")


    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("info.picocli:picocli:4.0.4")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-java:2.3.7")
    implementation("com.jayway.jsonpath:json-path:2.6.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.poi:poi-scratchpad:5.2.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("org.apache.pdfbox:pdfbox:2.0.24")


}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}


tasks.shadowJar {
    archiveFileName.set("unifile-all-1.0.0.jar")
    archiveVersion.set("1.0.0")
    manifest {
        attributes["Main-Class"] = "com.ingenifi.unifile.MainKt"
    }
}


version = "1.0.0"
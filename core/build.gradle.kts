plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Reflection
    implementation(kotlin("reflect"))

    // KTOR
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.7")

    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")

    // Dependency Injection
    implementation("com.google.inject:guice:7.0.0")

    // Configuration
    implementation("com.typesafe:config:1.4.3")
}
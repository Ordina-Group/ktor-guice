plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":plugins:json"))

    implementation("com.google.inject:guice:7.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Should not require this dependency!
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
}

kotlin {
    jvmToolchain(17)
}
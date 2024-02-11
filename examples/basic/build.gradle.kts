plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":plugins:json"))

    implementation("com.google.inject:guice:7.0.0")

    // Should not require this dependency!
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
}

kotlin {
    jvmToolchain(17)
}
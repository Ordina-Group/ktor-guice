group = "com.ordina.kuice"
version = "0.0.1"

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    jvmToolchain(jdkVersion = 17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "kuice-${project.name}"
            from(components["kotlin"])
        }
    }
}
plugins {
    id("library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("com.google.inject:guice:7.0.0")
    api("io.ktor:ktor-server-auth:2.3.7")
    api("io.ktor:ktor-server-auth-jwt:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

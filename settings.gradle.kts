plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ktor-guice"

include("core")

include("plugins:json")

include("plugins:websocket")

// Authentication Plugins
include("plugins:authentication:jwt")

include("examples:basic")

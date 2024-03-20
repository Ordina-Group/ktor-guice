plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "kuice"

include("core")

// Plugins
include("plugins:websocket")


// Serialization Plugins
include("plugins:serialization:json")
project(":plugins:serialization:json").name = "serialization-json"

// Authentication Plugins
include("plugins:authentication:jwt")
project(":plugins:authentication:jwt").name = "authentication-jwt"

// Examples
include("examples:basic")
include("examples:authentication")
include("examples:websocket")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "kuice"

include("core")

// Web Socket Plugins
include("plugins:websocket:core")
project(":plugins:websocket:core").name = "websocket-core"

include("plugins:websocket:json")
project(":plugins:websocket:json").name = "websocket-json"

// Serialization Plugins
include("plugins:serialization:json")
project(":plugins:serialization:json").name = "serialization-json"

// Authentication Plugins
include("plugins:authentication:jwt")
project(":plugins:authentication:jwt").name = "authentication-jwt"

// Examples
include("examples:basic")
include("examples:nested-routes")
include("examples:authentication")
include("examples:websocket")

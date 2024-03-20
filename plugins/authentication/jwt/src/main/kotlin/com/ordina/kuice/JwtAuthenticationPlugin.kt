package com.ordina.kuice

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.inject.Inject
import com.google.inject.ProvidedBy
import com.ordina.kuice.ktor.routes.BaseController
import com.ordina.kuice.config.ConfigLoader
import com.ordina.kuice.config.ConfigProvider
import com.ordina.kuice.ktor.plugins.BaseApplicationPlugin
import com.ordina.kuice.ktor.routes.RouteScope
import com.typesafe.config.Config
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationStrategy
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.Route

class JwtAuthenticationPlugin @Inject constructor(config: JwtAuthenticationConfig) :
    BaseApplicationPlugin<AuthenticationConfig, Authentication>(Authentication, {
    jwt {
        realm = config.realm
        verifier(
            JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()
        )
        validate { credential ->
            JWTPrincipal(credential.payload)
        }
    }
})

@ProvidedBy(JwtAuthenticationConfigProvider::class)
data class JwtAuthenticationConfig(
    val realm: String,
    val audience: String,
    val issuer: String,
    val secret: String
)

class JwtAuthenticationConfigProvider @Inject constructor(config: Config):
    ConfigProvider<JwtAuthenticationConfig>(JwtAuthenticationConfigurationLoader, config)

object JwtAuthenticationConfigurationLoader :
    ConfigLoader<JwtAuthenticationConfig>("ktor.authentication.jwt", {
        JwtAuthenticationConfig(
            realm = getString("realm"),
            audience = getString("audience"),
            issuer = getString("issuer"),
            secret = getString("secret")
        )
})

fun RouteScope.authenticate(n: RouteScope.() -> Unit) : Route.() -> Unit = {
    this.authenticate { n }
}

fun BaseController<*>.authenticatedRequest(
    vararg configurations: String? = arrayOf<String?>(null),
    strategy: AuthenticationStrategy = AuthenticationStrategy.FirstSuccessful,
    build: Route.() -> Unit
) : Route.() -> Unit = {
    authenticate(
        configurations = configurations,
        strategy = strategy,
        build = build
    )
}

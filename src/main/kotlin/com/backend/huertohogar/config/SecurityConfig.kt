package com.backend.huertohogar.config

import com.backend.huertohogar.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) } // Configuración CORS explícita
            .authorizeHttpRequests { auth ->
                auth
                    // 1. Rutas totalmente públicas (Login, Registro, Swagger)
                    .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                    // 2. ¡ESTA ES LA QUE TE FALTABA! Permitir VER productos sin estar logueado
                    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                    // 3. Cualquier otra cosa (Crear/Borrar productos) requiere login
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // 1. En lugar de "*", ponemos explícitamente tu frontend
        configuration.allowedOrigins = listOf("http://localhost:3000")

        // 2. Métodos permitidos
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")

        // 3. Headers permitidos (Authorization es clave para el JWT)
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")

        // 4. Permitir credenciales (True es necesario para que React se sienta seguro enviando datos)
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
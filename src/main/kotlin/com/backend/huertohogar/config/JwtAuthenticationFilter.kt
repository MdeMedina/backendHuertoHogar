package com.backend.huertohogar.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. Buscar el header "Authorization"
        val authHeader = request.getHeader("Authorization")

        // 2. Si no hay header o no empieza con "Bearer ", pasamos al siguiente filtro (Spring Security rechazará si es ruta privada)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // 3. Extraer el token (quitando "Bearer ")
        val jwt = authHeader.substring(7)

        // 4. Extraer el email del usuario desde el token
        // (Si el token es inválido, aquí saltará una excepción que debes manejar o dejar que Spring la capture)
        val userEmail = jwtService.extractUsername(jwt)

        // 5. Si hay usuario y aún no está autenticado en el contexto actual...
        if (SecurityContextHolder.getContext().authentication == null) {
            // Cargar los detalles del usuario desde la BD
            val userDetails = this.userDetailsService.loadUserByUsername(userEmail)

            // 6. Verificar si el token es válido para ese usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crear objeto de autenticación
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                // 7. Establecer la autenticación en el contexto de Spring (¡Usuario logueado!)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response)
    }
}
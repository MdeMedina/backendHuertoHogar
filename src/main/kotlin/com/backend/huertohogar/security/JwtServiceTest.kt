package com.backend.huertohogar.security

import com.backend.huertohogar.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtServiceTest {

    // Instancia real, no Mock (porque queremos probar su lógica interna)
    private val jwtService = JwtService()

    @Test
    fun `generateToken deberia crear un token valido para el usuario`() {
        // 1. Crear usuario de prueba
        val user = User(
            id = "1",
            email = "test@jwt.cl",
            passwordHash = "pass",
            fullName = "Jwt Tester",
            roles = setOf("ROLE_CLIENT")
        )

        // 2. Generar token
        val token = jwtService.generateToken(user)

        // 3. Validar
        assertNotNull(token)
        assertTrue(token.length > 20) // El token debe ser largo

        // Verificar que podemos extraer el usuario de vuelta
        val username = jwtService.extractUsername(token)
        assertEquals("test@jwt.cl", username)

        // Verificar validez
        assertTrue(jwtService.isTokenValid(token, user))
    }

    @Test
    fun `isTokenValid deberia retornar false si el usuario no coincide`() {
        val user1 = User(id="1", email="juan@test.cl", passwordHash="p", fullName="Juan")
        val user2 = User(id="2", email="pedro@test.cl", passwordHash="p", fullName="Pedro")

        val tokenDeJuan = jwtService.generateToken(user1)

        // Intentamos validar el token de Juan con el usuario Pedro
        assertFalse(jwtService.isTokenValid(tokenDeJuan, user2))
    }
}
package com.backend.huertohogar.dto


// Lo que el usuario envía para registrarse
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val address: String? = null,
    val phoneNumber: String? = null,
    // Un truco para tu examen: permitir enviar el rol desde el registro
    // En la vida real esto no se hace así, pero para probar rápido sirve.
    val role: String = "ROLE_CLIENT"
)

// Lo que el usuario envía para loguearse
data class LoginRequest(
    val email: String,
    val password: String
)

// Lo que tu backend responde (El famoso Token)
data class AuthResponse(
    val token: String
)
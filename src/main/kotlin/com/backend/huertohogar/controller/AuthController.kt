package com.backend.huertohogar.controller

import com.backend.huertohogar.dto.AuthResponse
import com.backend.huertohogar.dto.LoginRequest
import com.backend.huertohogar.dto.RegisterRequest
import com.backend.huertohogar.model.User
import com.backend.huertohogar.repository.UserRepository
import com.backend.huertohogar.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        // 1. Crear el objeto Usuario
        val user = User(
            email = request.email,
            // Importante: ¡Encriptar la contraseña antes de guardar!
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            address = request.address,
            phoneNumber = request.phoneNumber,
            roles = setOf(request.role) // Ej: "ROLE_ADMIN" o "ROLE_CLIENT"
        )

        // 2. Guardar en Mongo
        userRepository.save(user)

        // 3. Generar token automático para que quede logueado de una vez
        val token = jwtService.generateToken(user)

        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        // 1. Autenticar (Esto valida usuario y contraseña automáticamente)
        // Si falla, Spring lanza una excepción (403 Forbidden)
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        // 2. Si pasó la línea anterior, las credenciales son correctas. Buscamos al usuario.
        val user = userRepository.findByEmail(request.email)
            .orElseThrow() // Aquí ya sabemos que existe

        // 3. Generar Token
        val token = jwtService.generateToken(user)

        return ResponseEntity.ok(AuthResponse(token))
    }
}
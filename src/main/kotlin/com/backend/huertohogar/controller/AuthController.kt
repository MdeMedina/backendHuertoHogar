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
import org.springframework.security.core.userdetails.UsernameNotFoundException // <--- IMPORTANTE
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
        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            address = request.address,
            phoneNumber = request.phoneNumber,
            roles = setOf(request.role)
        )

        userRepository.save(user)
        val token = jwtService.generateToken(user)

        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        // CORRECCIÓN AQUÍ: Agregamos la lambda { ... }
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UsernameNotFoundException("Usuario no encontrado") }

        val token = jwtService.generateToken(user)

        return ResponseEntity.ok(AuthResponse(token))
    }
}
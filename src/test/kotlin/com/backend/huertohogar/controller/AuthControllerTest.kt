package com.backend.huertohogar.controller

import com.backend.huertohogar.dto.LoginRequest
import com.backend.huertohogar.dto.RegisterRequest
import com.backend.huertohogar.model.User
import com.backend.huertohogar.repository.UserRepository
import com.backend.huertohogar.security.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    // SOLUCIÓN: Instanciamos ObjectMapper manualmente para evitar errores de Autowire
    private val objectMapper = ObjectMapper()

    @MockitoBean
    lateinit var userRepository: UserRepository

    @MockitoBean
    lateinit var jwtService: JwtService

    @MockitoBean
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `login deberia devolver token cuando credenciales son validas`() {
        val loginRequest = LoginRequest("test@duoc.cl", "123456")
        val mockUser = User(id="1", email="test@duoc.cl", passwordHash="encodedPass", fullName="Tester")

        `when`(userRepository.findByEmail("test@duoc.cl")).thenReturn(Optional.of(mockUser))
        `when`(jwtService.generateToken(any())).thenReturn("TOKEN_FALSO_DE_PRUEBA")

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("TOKEN_FALSO_DE_PRUEBA"))
    }

    @Test
    fun `register deberia crear usuario y devolver token`() {
        val registerRequest = RegisterRequest("nuevo@duoc.cl", "123", "Nuevo User")

        `when`(jwtService.generateToken(any())).thenReturn("TOKEN_NUEVO")
        `when`(passwordEncoder.encode(any())).thenReturn("pass_encriptada")
        `when`(userRepository.save(any())).thenAnswer { it.arguments[0] }

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("TOKEN_NUEVO"))
    }
}
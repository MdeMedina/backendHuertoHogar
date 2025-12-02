package com.backend.huertohogar.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,

    @Indexed(unique = true) // Importante: no queremos emails repetidos
    val email: String,

    // Esta contraseña estará encriptada (BCrypt)
    private val passwordHash: String?,

    val fullName: String,

    // Datos del perfil requeridos por el caso
    val address: String? = null,
    val phoneNumber: String? = null,

    // Roles: ["ROLE_CLIENT", "ROLE_ADMIN"]
    val roles: Set<String> = setOf("ROLE_CLIENT")

) : UserDetails {

    // Métodos obligatorios de UserDetails para que Spring Security funcione

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it) }
    }

    override fun getPassword(): String? = passwordHash

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
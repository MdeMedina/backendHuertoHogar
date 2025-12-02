package com.backend.huertohogar.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    // (Mantén tu SECRET_KEY igual)
    private val SECRET_KEY = "HuertoHogarSecretoParaLaPruebaDiciembre2025"

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    // --- AQUÍ ESTÁ LA CORRECCIÓN ---
    fun generateToken(userDetails: UserDetails): String {
        val extraClaims: MutableMap<String, Any> = HashMap()

        // Extraemos el primer rol que tenga el usuario (Ej: "ROLE_ADMIN")
        // userDetails.authorities es una lista, tomamos el primero para simplificar
        val role = userDetails.authorities.firstOrNull()?.authority

        if (role != null) {
            extraClaims["roles"] = role // Guardamos "ROLE_ADMIN" o "ROLE_CLIENT"
        }

        return generateToken(extraClaims, userDetails)
    }
    // ------------------------------

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder()
            .claims(extraClaims) // Aquí se inyectarán los roles que pasamos arriba
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    // (El resto de los métodos se quedan igual: isTokenValid, isTokenExpired, etc.)
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun getSignInKey(): SecretKey {
        return Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
    }
}
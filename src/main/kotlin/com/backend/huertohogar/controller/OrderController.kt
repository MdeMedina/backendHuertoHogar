package com.backend.huertohogar.controller

import com.backend.huertohogar.model.Order
import com.backend.huertohogar.model.User
import com.backend.huertohogar.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

// DTO simple para recibir la compra
data class CreateOrderRequest(
    val items: Map<String, Int> // { "ID_PRODUCTO": CANTIDAD }
)

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @Operation(summary = "Crear pedido", description = "El usuario compra productos. Envía un mapa con ID_PRODUCTO y CANTIDAD.")
    fun createOrder(@RequestBody request: CreateOrderRequest, authentication: Authentication): ResponseEntity<Order> {
        // Extraemos el usuario del token
        val user = authentication.principal as User
        // Creamos la orden a su nombre (el ID puede ser nulo en el modelo, asegurate de manejarlo o que Mongo lo haya generado)
        val userId = user.id ?: throw RuntimeException("Error de usuario sin ID")

        val order = orderService.createOrder(userId, request.items)
        return ResponseEntity.ok(order)
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Mis pedidos", description = "Ver el historial de compras del usuario logueado")
    fun getMyOrders(authentication: Authentication): List<Order> {
        val user = authentication.principal as User
        val userId = user.id ?: ""
        return orderService.getOrdersByUser(userId)
    }

    // --- SOLO ADMIN ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver todos los pedidos", description = "Lista completa de ventas (Solo ADMIN)")
    fun getAllOrders(): List<Order> {
        return orderService.getAllOrders()
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado", description = "Cambiar estado del pedido (Ej: EN_CAMINO)")
    fun updateStatus(@PathVariable id: String, @RequestParam status: String): ResponseEntity<Order> {
        return orderService.updateOrderStatus(id, status)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }
}
package com.backend.huertohogar.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "orders")
data class Order(
    @Id
    val id: String? = null,

    val userId: String, // El ID del usuario que compró

    val items: List<OrderItem>, // Lista de productos comprados

    val totalAmount: Int, // Total a pagar (suma de los items)

    // Estados: "PENDIENTE", "EN_PREPARACION", "EN_CAMINO", "ENTREGADO"
    val status: String = "PENDIENTE",

    val createdAt: Date = Date()
)

// Clase auxiliar para guardar el detalle (snapshot del producto al momento de compra)
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val priceAtPurchase: Int // Guardamos el precio histórico
)
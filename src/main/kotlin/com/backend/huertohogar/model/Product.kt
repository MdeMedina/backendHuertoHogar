package com.backend.huertohogar.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "products")
data class Product(
    @Id
    val id: String? = null, // MongoDB lo genera automático si es nulo

    // Identificador visual (Ej: "FR001")
    val sku: String,

    val name: String,

    val description: String,

    val price: Int,

    val stock: Int,

    // Requisito específico del caso: "Mostrar el lugar de origen"
    val origin: String,

    // Categorías: Frutas, Verduras, Orgánicos, etc.
    val category: String,

    // URL de la imagen para que el frontend la muestre
    val imageUrl: String? = null
)
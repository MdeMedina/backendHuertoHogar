package com.backend.huertohogar.repository

import com.backend.huertohogar.model.Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : MongoRepository<Product, String> {

    // Métodos extra que podrían servirte para "filtros" en el futuro:

    // Buscar por categoría (ej: "Frutas")
    fun findByCategory(category: String): List<Product>

    // Buscar productos que contengan cierto texto en el nombre (Buscador)
    fun findByNameContainingIgnoreCase(name: String): List<Product>
}
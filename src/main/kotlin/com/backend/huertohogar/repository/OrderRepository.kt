package com.backend.huertohogar.repository

import com.backend.huertohogar.model.Order
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : MongoRepository<Order, String> {
    // Buscar historial de un cliente
    fun findByUserId(userId: String): List<Order>
}
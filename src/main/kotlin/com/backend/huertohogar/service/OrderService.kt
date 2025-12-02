package com.backend.huertohogar.service

import com.backend.huertohogar.model.Order
import com.backend.huertohogar.model.OrderItem
import com.backend.huertohogar.repository.OrderRepository
import com.backend.huertohogar.repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {

    // 1. Crear un pedido (Lógica de negocio compleja)
    // Recibimos un mapa de { productId -> cantidad } y el ID del usuario
    fun createOrder(userId: String, itemsRequest: Map<String, Int>): Order {
        val orderItems = mutableListOf<OrderItem>()
        var total = 0

        // Iteramos los productos pedidos
        itemsRequest.forEach { (productId, quantity) ->
            val product = productRepository.findById(productId).orElseThrow {
                RuntimeException("Producto no encontrado: $productId")
            }

            // Validar stock (Opcional pero recomendado)
            if (product.stock < quantity) {
                throw RuntimeException("Stock insuficiente para: ${product.name}")
            }

            // Descontar stock y guardar producto actualizado
            val updatedProduct = product.copy(stock = product.stock - quantity)
            productRepository.save(updatedProduct)

            // Crear el item del pedido
            val item = OrderItem(
                productId = product.id!!, // Asumimos que ya tiene ID
                productName = product.name,
                quantity = quantity,
                priceAtPurchase = product.price
            )
            orderItems.add(item)
            total += (item.priceAtPurchase * quantity)
        }

        // Guardar el pedido final
        val newOrder = Order(
            userId = userId,
            items = orderItems,
            totalAmount = total
        )
        return orderRepository.save(newOrder)
    }

    // 2. Obtener pedidos de un usuario
    fun getOrdersByUser(userId: String): List<Order> {
        return orderRepository.findByUserId(userId)
    }

    // 3. Obtener TODOS los pedidos (Para el Admin)
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    // 4. Cambiar estado (Para el Admin: de Pendiente a Entregado)
    fun updateOrderStatus(orderId: String, newStatus: String): Optional<Order> {
        return orderRepository.findById(orderId).map { order ->
            val updatedOrder = order.copy(status = newStatus)
            orderRepository.save(updatedOrder)
        }
    }
}
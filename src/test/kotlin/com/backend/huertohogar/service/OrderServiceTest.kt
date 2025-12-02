package com.backend.huertohogar.service

import com.backend.huertohogar.model.Order
import com.backend.huertohogar.model.Product
import com.backend.huertohogar.repository.OrderRepository
import com.backend.huertohogar.repository.ProductRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class OrderServiceTest {

    @Mock
    lateinit var orderRepository: OrderRepository

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var orderService: OrderService

    @Test
    fun `createOrder deberia crear orden y descontar stock correctamente`() {
        // 1. GIVEN (Datos de prueba)
        val userId = "user123"
        val productId = "prod1"
        // Simulamos que el usuario pide 2 unidades
        val itemsRequest = mapOf(productId to 2)

        // Producto original en BD con stock de 10
        val productoOriginal = Product(
            id = productId,
            sku = "TEST",
            name = "Manzana",
            description = "D",
            price = 1000,
            stock = 10,
            origin = "Curico",
            category = "Frutas"
        )

        // Mock: Cuando busquen el producto, devolvemos el original
        Mockito.`when`(productRepository.findById(productId)).thenReturn(Optional.of(productoOriginal))
        // Mock: Cuando guarden la orden, devolvemos una orden simulada
        Mockito.`when`(orderRepository.save(ArgumentMatchers.any(Order::class.java))).thenAnswer { it.arguments[0] }

        // 2. WHEN (Ejecutar la acción)
        val ordenCreada = orderService.createOrder(userId, itemsRequest)

        // 3. THEN (Verificaciones)
        // El total debe ser 2000 (1000 precio * 2 cantidad)
        Assertions.assertEquals(2000, ordenCreada.totalAmount)
        Assertions.assertEquals(1, ordenCreada.items.size)

        // ¡CRÍTICO! Verificar que se llamó a guardar el producto con el stock actualizado (10 - 2 = 8)
        Mockito.verify(productRepository).save(ArgumentMatchers.argThat { p -> p.stock == 8 })
        Mockito.verify(orderRepository).save(ArgumentMatchers.any(Order::class.java))
    }

    @Test
    fun `createOrder deberia fallar si no hay stock suficiente`() {
        val productId = "prod1"
        val itemsRequest = mapOf(productId to 20) // Pide 20, hay 10

        val productoOriginal = Product(
            id = productId,
            sku = "T",
            name = "Pera",
            description = "D",
            price = 500,
            stock = 10,
            origin = "X",
            category = "Y"
        )

        Mockito.`when`(productRepository.findById(productId)).thenReturn(Optional.of(productoOriginal))

        // Verificamos que lance excepción
        Assertions.assertThrows(RuntimeException::class.java) {
            orderService.createOrder("user1", itemsRequest)
        }

        // Aseguramos que NUNCA se guardó la orden
        Mockito.verify(orderRepository, Mockito.never()).save(ArgumentMatchers.any())
    }
}
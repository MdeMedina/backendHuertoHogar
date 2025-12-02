package com.backend.huertohogar.service

import com.backend.huertohogar.model.Product
import com.backend.huertohogar.repository.ProductRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun `getAllProducts deberia devolver lista de productos`() {
        // 1. Given (Dado que)
        val mockProducts = listOf(
            Product(
                sku = "TEST1",
                name = "Pera",
                description = "D",
                price = 100,
                stock = 10,
                origin = "A",
                category = "F"
            ),
            Product(
                sku = "TEST2",
                name = "Uva",
                description = "D",
                price = 200,
                stock = 20,
                origin = "B",
                category = "F"
            )
        )
        Mockito.`when`(productRepository.findAll()).thenReturn(mockProducts)

        // 2. When (Cuando)
        val resultado = productService.getAllProducts()

        // 3. Then (Entonces)
        Assertions.assertEquals(2, resultado.size)
        Mockito.verify(productRepository).findAll() // Verificamos que el repo fue llamado
    }

    @Test
    fun `createProduct deberia guardar y devolver el producto`() {
        val productoNuevo = Product(
            sku = "NUEVO",
            name = "Sandia",
            description = "D",
            price = 1000,
            stock = 5,
            origin = "C",
            category = "F"
        )
        Mockito.`when`(productRepository.save(productoNuevo)).thenReturn(productoNuevo)

        val resultado = productService.createProduct(productoNuevo)

        Assertions.assertNotNull(resultado)
        Assertions.assertEquals("Sandia", resultado.name)
    }

    @Test
    fun `deleteProduct deberia devolver true si existe`() {
        val id = "existe-123"
        Mockito.`when`(productRepository.existsById(id)).thenReturn(true)
        // No necesitamos mockear deleteById porque devuelve void

        val resultado = productService.deleteProduct(id)

        Assertions.assertTrue(resultado)
        Mockito.verify(productRepository).deleteById(id)
    }
}
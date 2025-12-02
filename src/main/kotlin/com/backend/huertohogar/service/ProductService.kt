package com.backend.huertohogar.service

import com.backend.huertohogar.model.Product
import com.backend.huertohogar.repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    // 1. Obtener todos los productos
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    // 2. Obtener un producto por ID
    fun getProductById(id: String): Optional<Product> {
        return productRepository.findById(id)
    }

    // 3. Crear un nuevo producto
    fun createProduct(product: Product): Product {
        // Aquí podrías agregar validaciones (ej: que el stock no sea negativo)
        return productRepository.save(product)
    }

    // 4. Actualizar un producto existente
    fun updateProduct(id: String, productDetails: Product): Optional<Product> {
        return productRepository.findById(id).map { existingProduct ->
            // Usamos copy de Kotlin para crear un nuevo objeto con los datos actualizados
            val updatedProduct = existingProduct.copy(
                sku = productDetails.sku,
                name = productDetails.name,
                description = productDetails.description,
                price = productDetails.price,
                stock = productDetails.stock,
                origin = productDetails.origin,
                category = productDetails.category,
                imageUrl = productDetails.imageUrl
            )
            productRepository.save(updatedProduct)
        }
    }

    // 5. Eliminar un producto
    fun deleteProduct(id: String): Boolean {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            return true
        }
        return false
    }
}
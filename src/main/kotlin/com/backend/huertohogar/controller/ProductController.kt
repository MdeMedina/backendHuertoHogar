package com.backend.huertohogar.controller

import com.backend.huertohogar.model.Product
import com.backend.huertohogar.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth") // Esto pone el candadito en Swagger para todos estos endpoints
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene el catálogo completo de HuertoHogar")
    fun getAllProducts(): List<Product> {
        return productService.getAllProducts()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Busca un producto específico por su ID")
    fun getProductById(@PathVariable id: String): ResponseEntity<Product> {
        return productService.getProductById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    // --- ENDPOINTS PROTEGIDOS (SOLO ADMIN) ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo el ADMIN puede crear
    @Operation(summary = "Crear producto", description = "Agrega un nuevo producto al inventario (Solo ADMIN)")
    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        val newProduct = productService.createProduct(product)
        return ResponseEntity.ok(newProduct)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo el ADMIN puede editar
    @Operation(summary = "Actualizar producto", description = "Modifica un producto existente (Solo ADMIN)")
    fun updateProduct(@PathVariable id: String, @RequestBody product: Product): ResponseEntity<Product> {
        return productService.updateProduct(id, product)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo el ADMIN puede borrar
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema (Solo ADMIN)")
    fun deleteProduct(@PathVariable id: String): ResponseEntity<Void> {
        return if (productService.deleteProduct(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
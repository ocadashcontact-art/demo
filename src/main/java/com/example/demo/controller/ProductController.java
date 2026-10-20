
package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las solicitudes HTTP
 * relacionadas con los productos de la tienda.
 *
 * Proporciona operaciones para consultar, crear, actualizar
 * y eliminar productos.
 *
 * @author API Tienda
 */
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Obtiene la lista de todos los productos.
     *
     * @return lista de productos registrados
     */
    @GetMapping("/products")
    public List<Product> getAll() {
        return productService.getProducts();
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */
    @GetMapping("/products/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * Registra un nuevo producto.
     *
     * @param name nombre del producto
     * @param price precio del producto
     * @return producto creado
     */
    @PostMapping("/products")
    public Product create(
            @RequestParam String name,
            @RequestParam double price) {
        return productService.createProduct(name, price);
    }

    /**
     * Actualiza el nombre y el precio de un producto existente.
     *
     * @param id identificador del producto
     * @param name nuevo nombre
     * @param price nuevo precio
     * @return producto actualizado
     */
    @PutMapping("/products/{id}")
    public Product update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam double price) {
        return productService.updateProduct(id, name, price);
    }

    /**
     * Actualiza parcialmente un producto.
     *
     * Se debe indicar al menos uno de los campos disponibles:
     * nombre o precio.
     *
     * @param id identificador del producto
     * @param name nuevo nombre, opcional
     * @param price nuevo precio, opcional
     * @return producto actualizado
     * @throws IllegalArgumentException si no se proporciona
     * ningún campo para actualizar
     */
    @PatchMapping("/products/{id}")
    public Product patch(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price) {

        if (name == null && price == null) {
            throw new IllegalArgumentException(
                    "Debe indicar al menos un campo para actualizar."
            );
        }

        return productService.patchProduct(id, name, price);
    }

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto
     * @return respuesta HTTP 204 sin contenido
     */
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}

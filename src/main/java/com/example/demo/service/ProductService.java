
package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Servicio encargado de administrar los productos de la tienda.
 *
 * Implementa las operaciones de consulta, creación, actualización
 * y eliminación, aplicando las validaciones correspondientes.
 *
 * @author API Tienda
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Inicializa el servicio con los repositorios necesarios.
     *
     * @param productRepository repositorio de productos
     * @param orderItemRepository repositorio de artículos de pedidos
     */
    public ProductService(
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista de productos
     */
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     * @throws ResponseStatusException si el producto no existe (404)
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Producto no encontrado."
                ));
    }

    /**
     * Crea un producto después de validar su nombre y precio.
     *
     * @param name nombre del producto
     * @param price precio del producto
     * @return producto guardado
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public Product createProduct(String name, double price) {
        validateProduct(name, price);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        return productRepository.save(product);
    }

    /**
     * Actualiza el nombre y el precio de un producto existente.
     *
     * @param id identificador del producto
     * @param name nuevo nombre
     * @param price nuevo precio
     * @return producto actualizado
     * @throws ResponseStatusException si el producto no existe
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public Product updateProduct(Long id, String name, double price) {
        validateProduct(name, price);

        Product product = getProductById(id);
        product.setName(name);
        product.setPrice(price);

        return productRepository.save(product);
    }

    /**
     * Actualiza parcialmente el nombre o el precio de un producto.
     * Los campos que no se proporcionan conservan su valor actual.
     *
     * @param id identificador del producto
     * @param name nuevo nombre, o null para conservar el actual
     * @param price nuevo precio, o null para conservar el actual
     * @return producto actualizado
     * @throws ResponseStatusException si el producto no existe
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public Product patchProduct(Long id, String name, Double price) {
        Product product = getProductById(id);

        String updatedName = name == null ? product.getName() : name;
        double updatedPrice = price == null ? product.getPrice() : price;

        validateProduct(updatedName, updatedPrice);

        product.setName(updatedName);
        product.setPrice(updatedPrice);

        return productRepository.save(product);
    }

    /**
     * Elimina un producto si no está asociado a artículos de pedidos.
     *
     * @param id identificador del producto
     * @throws ResponseStatusException si el producto no existe (404)
     * o si está asociado a pedidos (409)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);

        boolean tienePedidos =
                orderItemRepository.existsByProductId(id);

        if (tienePedidos) {
            throw new ResponseStatusException(
                    CONFLICT,
                    "No se puede eliminar el producto porque está asociado a pedidos."
            );
        }

        productRepository.delete(product);
    }

    /**
     * Valida el nombre y el precio de un producto.
     *
     * @param name nombre que se validará
     * @param price precio que se validará
     * @throws IllegalArgumentException si el nombre está vacío
     * o el precio no es un número finito mayor que cero
     */
    private void validateProduct(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es requerido."
            );
        }

        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }
    }
}

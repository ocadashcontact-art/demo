
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un artículo dentro de un pedido.
 *
 * Relaciona un pedido con un producto y almacena la cantidad
 * solicitada y el precio unitario aplicado al momento de agregarlo.
 *
 * @author API Tienda
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Identificador único del artículo del pedido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pedido al que pertenece este artículo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Producto asociado a este artículo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Cantidad de unidades solicitadas del producto.
     */
    private Integer quantity;

    /**
     * Precio unitario del producto al agregarlo al pedido.
     */
    private double unitPrice;

    /**
     * Calcula el subtotal del artículo multiplicando
     * el precio unitario por la cantidad solicitada.
     *
     * @return subtotal del artículo
     */
    public double getSubtotal() {
        return unitPrice * quantity;
    }
}


package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un pedido de la tienda.
 *
 * Almacena el identificador, estado, total y fecha de creación
 * del pedido.
 *
 * @author API Tienda
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Identificador único del pedido, generado automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Estado actual del pedido.
     * Por defecto, el pedido se crea en estado CARRITO.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CARRITO;

    /**
     * Importe total del pedido, calculado a partir de sus artículos.
     */
    private double total = 0.0;

    /**
     * Fecha y hora de creación del pedido.
     */
    private LocalDateTime createdAt = LocalDateTime.now();
}

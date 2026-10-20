
package com.example.demo.model;

/**
 * Define los estados posibles de un pedido en la tienda.
 *
 * @author API Tienda
 */
public enum OrderStatus {

    /**
     * Pedido en preparación; permite agregar productos.
     */
    CARRITO,

    /**
     * Pedido confirmado y procesado.
     */
    CONFIRMADO,

    /**
     * Pedido cancelado antes de su confirmación.
     */
    CANCELADO
}

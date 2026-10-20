
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa la respuesta detallada de un pedido.
 *
 * Contiene la información general del pedido y la lista
 * de productos incluidos, con sus cantidades y subtotales.
 *
 * @author API Tienda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    /**
     * Identificador del pedido.
     */
    private Long id;

    /**
     * Estado actual del pedido.
     */
    private String status;

    /**
     * Importe total del pedido.
     */
    private double total;

    /**
     * Fecha y hora de creación del pedido.
     */
    private LocalDateTime createdAt;

    /**
     * Lista de artículos incluidos en el pedido.
     */
    private List<OrderItemResponse> items;

    /**
     * DTO que representa un producto incluido en un pedido.
     *
     * Contiene el identificador y nombre del producto,
     * la cantidad, el precio unitario y el subtotal.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {

        /**
         * Identificador del producto.
         */
        private Long productId;

        /**
         * Nombre del producto.
         */
        private String productName;

        /**
         * Cantidad de unidades solicitadas.
         */
        private Integer quantity;

        /**
         * Precio unitario del producto.
         */
        private double unitPrice;

        /**
         * Subtotal del producto según la cantidad solicitada.
         */
        private double subtotal;
    }
}

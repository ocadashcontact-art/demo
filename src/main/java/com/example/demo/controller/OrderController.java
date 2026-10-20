
package com.example.demo.controller;

import com.example.demo.dto.OrderResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar carritos y pedidos.
 *
 * Expone los endpoints para crear carritos, agregar productos,
 * consultar pedidos, confirmar compras y cancelar carritos.
 *
 * @author API Tienda
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea un carrito nuevo.
     *
     * @return carrito creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<Order> crearCarrito() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.crearCarrito());
    }

    /**
     * Agrega un producto a un carrito existente.
     *
     * El cuerpo JSON debe incluir productId y quantity.
     *
     * @param orderId identificador del pedido
     * @param datos mapa con el identificador del producto y la cantidad
     * @return artículo agregado con estado HTTP 201
     * @throws IllegalArgumentException si faltan datos o son inválidos
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> agregarProducto(
            @PathVariable Long orderId,
            @RequestBody Map<String, Integer> datos) {

        if (datos == null || datos.get("productId") == null
                || datos.get("quantity") == null) {
            throw new IllegalArgumentException(
                    "Debe enviar productId y quantity."
            );
        }

        Integer productId = datos.get("productId");
        Integer quantity = datos.get("quantity");

        if (orderId == null || orderId <= 0
                || productId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores y la cantidad deben ser mayores que cero."
            );
        }

        OrderItem item = orderService.agregarProducto(
                orderId,
                productId.longValue(),
                quantity
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    /**
     * Obtiene todos los pedidos registrados.
     *
     * @return lista de pedidos
     */
    @GetMapping
    public List<Order> listarPedidos() {
        return orderService.listarPedidos();
    }

    /**
     * Confirma un pedido y procesa el descuento del inventario.
     *
     * @param orderId identificador del pedido
     * @return pedido confirmado con estado HTTP 200
     */
    @PutMapping("/{orderId}/confirmar")
    public ResponseEntity<Order> confirmarPedido(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                orderService.confirmarPedido(orderId)
        );
    }

    /**
     * Cancela un pedido que se encuentre en estado CARRITO.
     *
     * @param orderId identificador del pedido
     * @return pedido cancelado con estado HTTP 200
     */
    @PutMapping("/{orderId}/cancelar")
    public ResponseEntity<Order> cancelarPedido(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                orderService.cancelarPedido(orderId)
        );
    }

    /**
     * Consulta un pedido por su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado con estado HTTP 200
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> obtenerPedido(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                orderService.obtenerPedido(orderId)
        );
    }

    /**
     * Consulta un pedido junto con sus productos y subtotales.
     *
     * @param orderId identificador del pedido
     * @return detalle del pedido con estado HTTP 200
     */
    @GetMapping("/{orderId}/detalle")
    public ResponseEntity<OrderResponse> obtenerDetalle(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                orderService.obtenerPedidoDetalle(orderId)
        );
    }
}


package com.example.demo.service;

import com.example.demo.dto.OrderResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar los carritos y pedidos de la tienda.
 *
 * Permite crear carritos, agregar productos, calcular totales,
 * confirmar pedidos, cancelar carritos y consultar pedidos.
 *
 * @author API Tienda
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    /**
     * Crea un carrito nuevo con estado CARRITO y total inicial cero.
     *
     * @return carrito guardado en la base de datos
     */
    public Order crearCarrito() {
        Order order = new Order();
        order.setStatus(OrderStatus.CARRITO);
        order.setTotal(0.0);

        return orderRepository.save(order);
    }

    /**
     * Agrega un producto a un carrito y recalcula su total.
     *
     * @param orderId identificador del pedido
     * @param productId identificador del producto
     * @param quantity cantidad solicitada
     * @return artículo del pedido guardado
     * @throws IllegalArgumentException si los identificadores o la cantidad
     * no son válidos
     * @throws IllegalStateException si el pedido no está en CARRITO
     * o el stock es insuficiente
     * @throws ResponseStatusException si el pedido o producto no existe
     */
    @Transactional
    public OrderItem agregarProducto(
            Long orderId,
            Long productId,
            Integer quantity) {

        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido debe ser mayor que cero."
            );
        }

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del producto debe ser mayor que cero."
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        Order order = obtenerPedidoExistente(orderId);

        if (order.getStatus() != OrderStatus.CARRITO) {
            throw new IllegalStateException(
                    "Solo se pueden modificar pedidos en estado CARRITO."
            );
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado."
                ));

        if (product.getStock() == null ||
                product.getStock() < quantity) {
            throw new IllegalStateException(
                    "Stock insuficiente para: " + product.getName()
            );
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());

        OrderItem savedItem = orderItemRepository.save(item);

        // El total se calcula en el servidor.
        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        double total = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();

        order.setTotal(total);
        orderRepository.save(order);

        return savedItem;
    }

    /**
     * Obtiene todos los pedidos registrados.
     *
     * @return lista de pedidos
     */
    public List<Order> listarPedidos() {
        return orderRepository.findAll();
    }

    /**
     * Confirma un pedido y descuenta el stock de sus productos.
     * Primero verifica que todos los productos tengan existencias
     * suficientes; después realiza los descuentos dentro de una transacción.
     *
     * @param orderId identificador del pedido que se confirmará
     * @return pedido confirmado
     * @throws IllegalArgumentException si el identificador no es válido
     * @throws IllegalStateException si el pedido no está en CARRITO,
     * está vacío o no hay stock suficiente
     * @throws ResponseStatusException si el pedido o un producto no existe
     */
    @Transactional
    public Order confirmarPedido(Long orderId) {

        Order order = obtenerPedidoExistente(orderId);

        if (order.getStatus() != OrderStatus.CARRITO) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar pedidos en estado CARRITO."
            );
        }

        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede confirmar un pedido vacío."
            );
        }

        // Primera fase: verificar todas las existencias.
        for (OrderItem item : items) {

            Product product = productRepository.findById(
                    item.getProduct().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no encontrado."
            ));

            if (product.getStock() == null ||
                    product.getStock() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Stock insuficiente para: " + product.getName()
                );
            }
        }

        // Segunda fase: descontar el stock.
        for (OrderItem item : items) {

            Product product = productRepository.findById(
                    item.getProduct().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no encontrado."
            ));

            product.setStock(
                    product.getStock() - item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CONFIRMADO);

        return orderRepository.save(order);
    }

    /**
     * Cancela un pedido que todavía se encuentra en estado CARRITO.
     *
     * @param orderId identificador del pedido que se cancelará
     * @return pedido con estado CANCELADO
     * @throws IllegalArgumentException si el identificador no es válido
     * @throws IllegalStateException si el pedido no está en CARRITO
     * @throws ResponseStatusException si el pedido no existe
     */
    @Transactional
    public Order cancelarPedido(Long orderId) {

        Order order = obtenerPedidoExistente(orderId);

        if (order.getStatus() != OrderStatus.CARRITO) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar pedidos en estado CARRITO."
            );
        }

        order.setStatus(OrderStatus.CANCELADO);

        return orderRepository.save(order);
    }

    /**
     * Busca un pedido por su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado
     * @throws IllegalArgumentException si el identificador no es válido
     * @throws ResponseStatusException si el pedido no existe (404)
     */
    public Order obtenerPedido(Long orderId) {
        return obtenerPedidoExistente(orderId);
    }

    /**
     * Obtiene un pedido junto con sus productos y subtotales.
     *
     * @param orderId identificador del pedido
     * @return respuesta DTO con el pedido y el detalle de sus artículos
     * @throws IllegalArgumentException si el identificador no es válido
     * @throws ResponseStatusException si el pedido no existe (404)
     */
    public OrderResponse obtenerPedidoDetalle(Long orderId) {

        Order order = obtenerPedidoExistente(orderId);

        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        List<OrderResponse.OrderItemResponse> detalles =
                items.stream()
                        .map(item -> new OrderResponse.OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal()
                        ))
                        .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                order.getCreatedAt(),
                detalles
        );
    }

    /**
     * Busca un pedido existente y valida su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado
     * @throws IllegalArgumentException si el identificador es nulo
     * o menor o igual que cero
     * @throws ResponseStatusException si no existe el pedido (404)
     */
    private Order obtenerPedidoExistente(Long orderId) {

        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido debe ser mayor que cero."
            );
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado."
                ));
    }
}

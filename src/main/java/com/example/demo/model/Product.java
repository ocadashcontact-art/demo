
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un producto de la tienda.
 *
 * Contiene la información básica del producto, incluyendo
 * su identificador, nombre, precio y existencias disponibles.
 *
 * @author API Tienda
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Identificador único del producto, generado automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto.
     */
    private String name;

    /**
     * Precio unitario del producto.
     */
    private double price;

    /**
     * Cantidad de unidades disponibles en el inventario.
     */
    private Integer stock;
}

package com.javaguides.springboot.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Clase PRICES que representa la entidad para la tabla 'prices' en la base de datos.
 * Esta clase contiene los detalles del precio de un producto, incluyendo su rango de vigencia,
 * la marca asociada y otros atributos como el identificador de producto, prioridad y moneda.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "prices")
public class PRICES {

    /**
     * Identificador único del precio en la tabla 'prices'.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long priceId;

    /**
     * Relación con la entidad BRAND, que representa la marca a la que pertenece este precio.
     * Un precio está asociado a una marca (Many-To-One).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", referencedColumnName = "id", nullable = false)
    private BRAND brand;

    /**
     * Fecha de inicio de vigencia del precio.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Fecha de fin de vigencia del precio.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * Identificador de la lista de precios aplicable.
     */
    @Column(name = "price_list", nullable = false)
    private int priceList;

    /**
     * Identificador del producto al que se aplica este precio.
     */
    @Column(name = "product_id", nullable = false)
    private long productId;

    /**
     * Valor que determina la prioridad en caso de solapamiento de precios en el mismo rango de fechas.
     * Se aplica el precio con la mayor prioridad (mayor valor numérico).
     */
    @Column(name = "priority", nullable = false)
    private int priority;

    /**
     * Precio final de venta del producto.
     */
    @Column(name = "price", nullable = false)
    private double price;

    /**
     * Código ISO de la moneda en la que se aplica el precio (por ejemplo, EUR para euros).
     */
    @Column(name = "curr", nullable = false)
    private String curr;
}

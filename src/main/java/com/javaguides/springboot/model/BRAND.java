package com.javaguides.springboot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase BRAND que representa la entidad para la tabla 'brand' en la base de datos.
 * Esta clase contiene la información sobre las marcas y su relación con los precios de productos.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brand")
public class BRAND {

    /**
     * Identificador único de la marca en la tabla 'brand'.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Nombre de la marca. Este campo no puede ser nulo.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Descripción de la marca. Este campo no puede ser nulo.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Relación One-To-Many con la entidad PRICES, lo que significa que una marca puede tener múltiples precios asociados.
     * La relación está configurada con cascade para que las operaciones de persistencia en la entidad BRAND afecten a los precios relacionados.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "brand")
    private Set<PRICES> prices = new HashSet<>();
}

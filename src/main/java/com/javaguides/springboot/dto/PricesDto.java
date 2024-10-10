package com.javaguides.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clase PricesDto usada en /src/main/java/com.javaguides.springboot/controller/PriceBrandController para la correcta
 * serialización y envío de datos REST. Contiene todos los datos relevantes de la Clase Price.
 */
@Schema(description = "Modelo PricesDto que contiene información sobre precios.")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PricesDto {

    @Schema(description = "Identificador del precio en la tabla.")
    private long priceId;

    @Schema(description = "Identificador de la cadena (brand), clave foránea.")
    private long brandId;

    @Schema(description = "Fecha de inicio de vigencia del precio.")
    private LocalDateTime startDate;

    @Schema(description = "Fecha de fin de vigencia del precio.")
    private LocalDateTime endDate;

    @Schema(description = "Lista de precios aplicable.")
    private int priceList;

    @Schema(description = "Identificador del código de producto.")
    private long productId;

    @Schema(description = "Prioridad para desambiguar precios, donde mayor valor numérico tiene mayor prioridad.")
    private int priority;

    @Schema(description = "Precio final de venta.")
    private double price;

    @Schema(description = "Código ISO de la moneda.")
    private String curr;
}

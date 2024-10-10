package com.javaguides.springboot.mapper;


import com.javaguides.springboot.dto.PricesExamDto;
import com.javaguides.springboot.model.PRICES;

/*
 * Clase PricesExamMapper usada en /src/main/java/com.javaguides.springboot/controller/PriceBrandController para la correcta
 * serialización y envío de datos REST, contiene todos los datos relevantes de la Clase Price para el examen
 */

public class PricesExamMapper {
    // Convert User JPA Entity into UserDto
    public static PricesExamDto mapToPricesExamDto(PRICES prices){
        PricesExamDto pricesExamDto = new PricesExamDto(
                prices.getProductId(),        // Cambiado de getPRODUCT_ID() a getProductId()
                prices.getBrand().getId(),     // Sin cambio, ya que getId() es correcto
                prices.getPriceList(),         // Cambiado de getPRICE_LIST() a getPriceList()
                prices.getPrice(),             // Sin cambio, ya que getPrice() es correcto
                prices.getStartDate(),         // Cambiado de getSTART_DATE() a getStartDate()
                prices.getEndDate()            // Cambiado de getEND_DATE() a getEndDate()
        );
        return pricesExamDto;
    }
}

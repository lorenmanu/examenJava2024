package com.javaguides.springboot.mapper;

import com.javaguides.springboot.dto.PricesDto;
import com.javaguides.springboot.model.BRAND;
import com.javaguides.springboot.model.PRICES;
import com.javaguides.springboot.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Clase PricesMapper para convertir entre PRICES (entidad JPA) y PricesDto.
 */
@Component
public class PricesMapper {

    private final BrandRepository brandRepository;

    // Inyección de dependencias mediante constructor
    @Autowired
    public PricesMapper(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /**
     * Convierte una entidad PRICES en un PricesDto.
     *
     * @param prices la entidad PRICES a convertir.
     * @return PricesDto con los datos correspondientes.
     */
    public static PricesDto mapToPricesDto(PRICES prices) {
        return new PricesDto(
                prices.getPriceId(),
                prices.getBrand().getId(),
                prices.getStartDate(),
                prices.getEndDate(),
                prices.getPriceList(),
                prices.getProductId(),
                prices.getPriority(),
                prices.getPrice(),
                prices.getCurr()
        );
    }

    /**
     * Convierte un PricesDto en una entidad PRICES.
     *
     * @param pricesDto el DTO a convertir en una entidad PRICES.
     * @return PRICES con los datos correspondientes.
     */
    public PRICES mapToPrices(PricesDto pricesDto) {
        // Buscar la entidad BRAND asociada al PricesDto
        BRAND brand = brandRepository.findById(pricesDto.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found for ID: " + pricesDto.getBrandId()));

        // Construir y devolver la entidad PRICES
        return PRICES.builder()
                .priceId(pricesDto.getPriceId())
                .brand(brand)
                .startDate(pricesDto.getStartDate())
                .endDate(pricesDto.getEndDate())
                .priceList(pricesDto.getPriceList())
                .productId(pricesDto.getProductId())
                .priority(pricesDto.getPriority())
                .price(pricesDto.getPrice())
                .curr(pricesDto.getCurr())
                .build();
    }
}

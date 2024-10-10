package com.javaguides.springboot.controller;

import com.javaguides.springboot.dto.PricesDto;
import com.javaguides.springboot.dto.PricesExamDto;
import com.javaguides.springboot.exception.ResourceNotFoundException;
import com.javaguides.springboot.mapper.PricesExamMapper;
import com.javaguides.springboot.mapper.PricesMapper;
import com.javaguides.springboot.model.BRAND;
import com.javaguides.springboot.model.PRICES;
import com.javaguides.springboot.service.PriceBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/*
 * Controlador PriceBrandController.
 * 
 * Este controlador expone varias APIs REST que permiten realizar operaciones relacionadas
 * con los precios y las marcas. Estas operaciones incluyen obtener precios, actualizar precios,
 * y buscar precios con base en distintos criterios como fecha, ID de producto, y ID de marca.
 */
@Tag(
    name = "PriceBrand Service - PriceBrandController",
    description = "PriceBrand Service - Exposes REST APIs for PriceBrand Service"
)
@RestController
@RequestMapping("/api/prices")
public class PriceBrandController {

    @Autowired
    private PriceBrandService priceBrandService;

    @Autowired
    private PricesMapper pricesMapper;

    /*
     * Endpoint para obtener todos los precios almacenados en la base de datos.
     * 
     * Este método obtiene una lista de todos los precios registrados, los convierte
     * en objetos DTO y los devuelve en una respuesta HTTP con el código 200 (OK).
     */
    @Operation(summary = "Get all prices", description = "Fetch all prices from the database")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/")
    public ResponseEntity<List<PricesDto>> getAllPrices() {
        List<PRICES> pricesList = priceBrandService.getAllPrices();
        List<PricesDto> pricesDtoList = pricesList.stream()
                .map(PricesMapper::mapToPricesDto)  // Llamada estática al mapper
                .toList();
        return ResponseEntity.ok(pricesDtoList);
    }

    /*
     * Endpoint para obtener un precio por su ID.
     * 
     * Busca en la base de datos un precio cuyo ID coincida con el proporcionado. 
     * Si el precio existe, devuelve el objeto en formato DTO con una respuesta 200 (OK).
     * Si no se encuentra el precio, lanza una excepción indicando que el recurso no existe.
     */
    @Operation(summary = "Get price by ID", description = "Fetch a price by its ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/{id}")
    public ResponseEntity<PricesDto> getPriceById(@PathVariable("id") long id) {
        PRICES price = priceBrandService.getPriceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found with ID: " + id));
        return ResponseEntity.ok(pricesMapper.mapToPricesDto(price));
    }

    /*
     * Endpoint para obtener un precio basado en la fecha de aplicación, ID de producto y ID de marca.
     * 
     * Busca en la base de datos el precio que coincida con los criterios proporcionados, incluyendo
     * la fecha, el ID de producto y el ID de marca. Si se encuentra un precio, lo devuelve en formato DTO.
     * Si no se encuentra, devuelve una respuesta con el estado 404 (Not Found).
     */
    @Operation(summary = "Get price by fechaAplicacion, prodId, brandId", description = "Fetch a price based on multiple criteria")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/fechaAppli/{fechaAplicacion}/{prodId}/{brandId}")
    public ResponseEntity<PricesExamDto> getPriceByCriteria(
            @PathVariable("fechaAplicacion") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaAplicacion,
            @PathVariable("prodId") long prodId,
            @PathVariable("brandId") long brandId) {

        PRICES price = priceBrandService.findByDateRequestIdProductIdBrand(fechaAplicacion, prodId, brandId);

        if (price != null) {
            PricesExamDto pricesExamDto = PricesExamMapper.mapToPricesExamDto(price);
            return ResponseEntity.ok(pricesExamDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PricesExamDto());
        }
    }

    /*
     * Endpoint para crear un nuevo precio y asociarlo a una marca.
     * 
     * Este método recibe los datos del nuevo precio y su marca asociada en formato DTO,
     * luego guarda ambos en la base de datos y devuelve el precio creado en la respuesta HTTP con estado 201 (Created).
     */
    @Operation(summary = "Create a new price", description = "Create a new price with an associated brand")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping("/price")
    public ResponseEntity<PricesDto> createPrice(@RequestBody PricesDto pricesDto) {
        PRICES priceToSave = pricesMapper.mapToPrices(pricesDto);
        BRAND brand = priceBrandService.getBrandById(pricesDto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + pricesDto.getBrandId()));
        
        priceToSave.setBrand(brand);
        priceBrandService.savePriceBrand(priceToSave, brand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pricesMapper.mapToPricesDto(priceToSave));
    }

    /*
     * Endpoint para actualizar un precio existente por su ID.
     * 
     * Este método actualiza los datos de un precio, asegurando que tanto el ID del precio como el ID de la marca existan.
     * Si el precio o la marca no se encuentran, lanza una excepción indicando que el recurso no existe.
     */
    @Operation(summary = "Update an existing price", description = "Update a price by its ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping("/price/{id}")
    public ResponseEntity<PricesDto> updatePrice(@PathVariable("id") long id, @RequestBody PricesDto pricesDto) {
        if (id != pricesDto.getPriceId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PRICES existingPrice = priceBrandService.getPriceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found with ID: " + id));

        BRAND brand = priceBrandService.getBrandById(pricesDto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + pricesDto.getBrandId()));

        existingPrice.setPrice(pricesDto.getPrice());
        existingPrice.setStartDate(pricesDto.getStartDate());
        existingPrice.setEndDate(pricesDto.getEndDate());
        existingPrice.setPriceList(pricesDto.getPriceList());
        existingPrice.setProductId(pricesDto.getProductId());
        existingPrice.setPriority(pricesDto.getPriority());
        existingPrice.setCurr(pricesDto.getCurr());
        existingPrice.setBrand(brand);

        PRICES updatedPrice = priceBrandService.updatePrice(existingPrice);

        return ResponseEntity.ok(pricesMapper.mapToPricesDto(updatedPrice));
    }

    /*
     * Endpoint para eliminar un precio por su ID.
     * 
     * Este método busca y elimina un precio de la base de datos. Si no se encuentra el precio, lanza una excepción
     * indicando que no existe el recurso. Devuelve una respuesta sin contenido (204 No Content).
     */
    @Operation(summary = "Delete a price by ID", description = "Delete a price by its ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @DeleteMapping("/price/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable("id") long id) {
        PRICES price = priceBrandService.getPriceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found with ID: " + id));

        priceBrandService.deletePrice(id);
        return ResponseEntity.noContent().build();
    }
}

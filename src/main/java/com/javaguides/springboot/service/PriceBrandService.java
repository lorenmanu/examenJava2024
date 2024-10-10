package com.javaguides.springboot.service;

import com.javaguides.springboot.model.BRAND;
import com.javaguides.springboot.model.PRICES;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Interfaz del servicio PriceBrandService para gestionar la lógica de negocio
 * relacionada con precios y marcas, permitiendo guardar, actualizar, eliminar
 * y buscar tanto marcas como precios.
 */
public interface PriceBrandService {

    /*
     * Guarda un precio y su relación con una marca en la base de datos.
     * Devuelve la marca asociada al precio guardado.
     */
    BRAND savePriceBrand(PRICES price, BRAND brand);

    /*
     * Guarda una marca en la base de datos.
     * Devuelve la marca guardada.
     */
    BRAND saveBrand(BRAND brand);

    /*
     * Devuelve una lista de todas las marcas registradas en la base de datos.
     */
    List<BRAND> getAllBrands();

    /*
     * Devuelve una lista de todos los precios registrados en la base de datos.
     */
    List<PRICES> getAllPrices();

    /*
     * Busca un precio por su ID y lo devuelve en un Optional.
     * Si no se encuentra, devuelve un Optional vacío.
     */
    Optional<PRICES> getPriceById(long id);

    /*
     * Busca una marca por su ID y la devuelve en un Optional.
     * Si no se encuentra, devuelve un Optional vacío.
     */
    Optional<BRAND> getBrandById(long id);

    /*
     * Actualiza un precio existente en la base de datos.
     * Devuelve el precio actualizado.
     */
    PRICES updatePrice(PRICES price);

    /*
     * Actualiza una marca existente en la base de datos.
     * Devuelve la marca actualizada.
     */
    BRAND updateBrand(BRAND brand);

    /*
     * Elimina un precio de la base de datos por su ID.
     */
    void deletePrice(long id);

    /*
     * Elimina una marca de la base de datos por su ID.
     */
    void deleteBrand(long id);

    /*
     * Busca un precio que corresponda a la fecha de aplicación, 
     * el ID del producto y el ID de la marca proporcionados.
     * Devuelve el precio que coincide con estos criterios.
     */
    PRICES findByDateRequestIdProductIdBrand(LocalDateTime fechaApli, long idProd, long idBrand);
}


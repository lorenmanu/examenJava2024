package com.javaguides.springboot.service.impl;

import com.javaguides.springboot.exception.ResourceNotFoundException;
import com.javaguides.springboot.model.BRAND;
import com.javaguides.springboot.model.PRICES;
import com.javaguides.springboot.repository.BrandRepository;
import com.javaguides.springboot.repository.PriceRepository;
import com.javaguides.springboot.service.PriceBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
 * Clase de implementación del servicio PriceBrandService.
 * 
 * Esta clase proporciona la lógica de negocio para gestionar los precios
 * y las marcas asociadas a ellos, utilizando los repositorios de datos correspondientes.
 */
@Service
public class PriceBrandServiceImpl implements PriceBrandService {
    
    @Autowired
    private PriceRepository priceRepository;
    
    @Autowired
    private BrandRepository brandRepository;

    /*
     * Constructor de la clase PriceBrandServiceImpl.
     * Recibe los repositorios que permiten la gestión de precios y marcas.
     */
    public PriceBrandServiceImpl(PriceRepository pricesRepository, BrandRepository brandRepository) {
        this.priceRepository = pricesRepository;
        this.brandRepository = brandRepository;
    }

    /*
     * Guarda un precio y su relación con una marca en la base de datos.
     * Si el precio ya existe, lanza una excepción.
     * Si la relación entre la marca y el precio no está definida, se establece.
     * Devuelve la marca asociada.
     */
    @Override
    public BRAND savePriceBrand(PRICES price, BRAND brand) {
        Optional<PRICES> priceBD = priceRepository.findById(price.getPriceId());
        Optional<BRAND> brandBD = brandRepository.findById(brand.getId());

        if (priceBD.isPresent()) {
            throw new ResourceNotFoundException("Price already exists in BD, ID -- " + price.getPriceId());
        }

        if (price.getBrand() == null || price.getBrand().getId() != brand.getId() || brand.getPrices() == null) {
            Set<PRICES> pricesSet = new HashSet<>();
            pricesSet.add(price);
            brand.setPrices(pricesSet);
            price.setBrand(brand);
        }

        brandRepository.save(brand);
        priceRepository.save(price);

        return brand;
    }

    /*
     * Guarda una marca en la base de datos y la devuelve.
     */
    @Override
    public BRAND saveBrand(BRAND brand) {
        return brandRepository.save(brand);
    }

    /*
     * Obtiene todas las marcas almacenadas en la base de datos.
     * Devuelve una lista de marcas.
     */
    @Override
    public List<BRAND> getAllBrands() {
        return brandRepository.findAll();
    }

    /*
     * Obtiene todos los precios almacenados en la base de datos.
     * Devuelve una lista de precios.
     */
    @Override
    public List<PRICES> getAllPrices() {
        return priceRepository.findAll();
    }

    /*
     * Busca un precio por su ID en la base de datos.
     * Devuelve un Optional con el precio encontrado o vacío si no existe.
     */
    @Override
    public Optional<PRICES> getPriceById(long id) {
        return priceRepository.findById(id);
    }

    /*
     * Busca una marca por su ID en la base de datos.
     * Devuelve un Optional con la marca encontrada o vacío si no existe.
     */
    @Override
    public Optional<BRAND> getBrandById(long id) {
        return brandRepository.findById(id);
    }

    /*
     * Actualiza un precio existente en la base de datos y lo devuelve.
     */
    @Override
    public PRICES updatePrice(PRICES price) {
        return priceRepository.save(price);
    }

    /*
     * Actualiza una marca existente en la base de datos y la devuelve.
     */
    @Override
    public BRAND updateBrand(BRAND brand) {
        return brandRepository.save(brand);
    }

    /*
     * Elimina un precio de la base de datos según su ID.
     */
    @Override
    public void deletePrice(long id) {
        priceRepository.deleteById(id);
    }

    /*
     * Elimina una marca de la base de datos según su ID.
     */
    @Override
    public void deleteBrand(long id) {
        brandRepository.deleteById(id);
    }

    /*
     * Busca un precio que coincida con la fecha, el ID del producto y el ID de la marca dados.
     * Si existen varios precios, devuelve el que tiene mayor prioridad.
     * Si no encuentra ningún precio, devuelve un objeto vacío.
     */
    @Override
    public PRICES findByDateRequestIdProductIdBrand(LocalDateTime fechaApli, long idProd, long idBrand) {
        List<PRICES> pricesList = priceRepository.findByDateRequestIdProductIdBrand(fechaApli, idProd, idBrand);
        PRICES priceFound = pricesList.size() > 0 ? pricesList.get(0) : new PRICES();

        for (PRICES price : pricesList) {
            if (price.getPriority() > priceFound.getPriority()) {
                priceFound = price;
            }
        }

        return priceFound;
    }
}

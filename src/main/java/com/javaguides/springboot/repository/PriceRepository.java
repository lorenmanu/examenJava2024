package com.javaguides.springboot.repository;

import com.javaguides.springboot.model.PRICES;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Clase PriceRepository usada para realizar consultas en la base de datos a la tabla PRICES
 */
@Repository
public interface PriceRepository extends JpaRepository<PRICES, Long> {
    List<PRICES> findByPrice(long price);

    @Query("SELECT p FROM PRICES p WHERE (:fechaApli BETWEEN p.startDate AND p.endDate) AND p.productId = :idProd AND p.brand.id = :idBrand")
    List<PRICES> findByDateRequestIdProductIdBrand(@Param("fechaApli") LocalDateTime fechaApli,
                                                   @Param("idProd") long idProd, @Param("idBrand") long idBrand);
}

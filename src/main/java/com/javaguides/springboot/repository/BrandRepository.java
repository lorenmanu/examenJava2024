package com.javaguides.springboot.repository;

import com.javaguides.springboot.model.BRAND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Clase BrandRepository usada para realizar consultas en la base de datos a la tabla BRAND
 */
@Repository
public interface BrandRepository extends JpaRepository<BRAND, Long> {
    Optional<BRAND> findById(long id);
}

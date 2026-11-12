package com.localflow.catalogue.repository;

import com.localflow.catalogue.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByBusiness_IdAndActiveTrueOrderByNameAsc(Long businessId);

    Optional<Product> findByIdAndBusiness_IdAndActiveTrue(Long id, Long businessId);

    boolean existsByBusiness_IdAndSkuIgnoreCase(Long businessId, String sku);

    boolean existsByBusiness_IdAndSkuIgnoreCaseAndIdNot(Long businessId, String sku, Long id);

    boolean existsByBusiness_IdAndCategory_Id(Long businessId, Long categoryId);

}

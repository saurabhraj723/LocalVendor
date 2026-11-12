package com.localflow.catalogue.repository;

import com.localflow.catalogue.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByBusiness_IdOrderBySortOrderAscNameAsc(Long businessId);

    Optional<Category> findByIdAndBusiness_Id(Long id, Long businessId);

    boolean existsByBusiness_IdAndNameIgnoreCase(Long businessId, String name);

    boolean existsByBusiness_IdAndNameIgnoreCaseAndIdNot(Long businessId, String name, Long id);
}

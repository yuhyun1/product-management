package com.productmanagement.domain.productoptionvalue.repository;

import com.productmanagement.domain.productoptionvalue.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
    Optional<ProductOptionValue> findByIdAndDeletedAtIsNull(Long valueId);
}

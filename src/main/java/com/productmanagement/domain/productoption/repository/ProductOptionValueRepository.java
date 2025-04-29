package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
}

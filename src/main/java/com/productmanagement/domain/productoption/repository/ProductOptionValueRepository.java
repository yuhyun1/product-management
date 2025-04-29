package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
    List<ProductOptionValue> findAllByProductOptionAndDeletedAtIsNull(ProductOption option);
}

package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.productoption.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    long countByProductAndDeletedAtIsNull(Product product);

}
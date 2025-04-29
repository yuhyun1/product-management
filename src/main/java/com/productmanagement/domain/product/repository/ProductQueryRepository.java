package com.productmanagement.domain.product.repository;

import com.productmanagement.domain.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductQueryRepository {

    Page<ProductResponse> findAllProducts(Pageable pageable);

    Optional<ProductResponse> findById(Long productId);

}
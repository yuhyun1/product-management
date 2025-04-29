package com.productmanagement.domain.product.repository;

import com.productmanagement.domain.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Page<ProductResponse> findAllProducts(Pageable pageable);
}
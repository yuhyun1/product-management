package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.dto.ProductOptionSummaryResponse;

import java.util.List;

public interface ProductOptionQueryRepository {
    List<ProductOptionSummaryResponse> findSummaryByProductId(Long productId);
}
package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.dto.ProductOptionValueResponse;

import java.util.List;

public interface ProductOptionValueQueryRepository {
    List<ProductOptionValueResponse> findAllByOptionId(Long optionId);

    void bulkSoftDeleteByOptionId(Long optionId);
}
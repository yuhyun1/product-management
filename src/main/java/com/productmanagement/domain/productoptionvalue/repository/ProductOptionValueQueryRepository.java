package com.productmanagement.domain.productoptionvalue.repository;

import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;

import java.util.List;

public interface ProductOptionValueQueryRepository {
    List<ProductOptionValueResponse> findAllByOptionId(Long optionId);

    void bulkSoftDeleteByOptionId(Long optionId);
}
package com.productmanagement.domain.productoption.dto;

import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;

import java.util.List;

public record ProductOptionDetailResponse(
    Long id,
    String name,
    OptionType type,
    Integer additionalPrice,
    List<ProductOptionValueResponse> values
) {
}
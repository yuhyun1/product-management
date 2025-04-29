package com.productmanagement.domain.productoption.dto;

import com.productmanagement.domain.productoption.entity.OptionType;

public record ProductOptionSummaryResponse(
    Long id,
    String name,
    OptionType type,
    Integer additionalPrice
) {
}
package com.productmanagement.domain.productoption.dto;

import com.productmanagement.domain.productoption.entity.OptionType;

import java.time.LocalDateTime;

public record ProductOptionUpdateResponse(
    Long id,
    String name,
    OptionType type,
    Integer additionalPrice,
    LocalDateTime updatedAt
) {
}
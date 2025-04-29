package com.productmanagement.domain.productoptionvalue.dto;

public record ProductOptionValueResponse(
    Long id,
    String value,
    Integer additionalPrice,
    Integer stock
) {
}
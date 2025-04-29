package com.productmanagement.domain.productoption.dto;

public record ProductOptionValueResponse(
    Long id,
    String value,
    Integer additionalPrice,
    Integer stock
) {
}
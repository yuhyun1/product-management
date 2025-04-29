package com.productmanagement.domain.productoptionvalue.dto;

import java.time.LocalDateTime;

public record ProductOptionValueUpdateResponse(
    Long id,
    String value,
    Integer additionalPrice,
    Integer stock,
    LocalDateTime updatedAt
) {
}
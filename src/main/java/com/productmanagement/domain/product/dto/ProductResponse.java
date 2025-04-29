package com.productmanagement.domain.product.dto;

import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    int price,
    int stock,
    int shippingFee,
    LocalDateTime createdAt
) {
}
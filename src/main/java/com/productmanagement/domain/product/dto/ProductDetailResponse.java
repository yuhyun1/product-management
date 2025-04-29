package com.productmanagement.domain.product.dto;

import com.productmanagement.domain.productoption.dto.ProductOptionSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponse(
    Long id,
    String name,
    String description,
    Integer price,
    Integer shippingFee,
    Integer stock,
    LocalDateTime createdAt,
    List<ProductOptionSummaryResponse> options
) {
}
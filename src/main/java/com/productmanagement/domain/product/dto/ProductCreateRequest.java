package com.productmanagement.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
    @NotBlank(message = "상품 이름은 필수입니다.")
    String name,

    @NotBlank(message = "상품 설명은 필수입니다.")
    String description,

    @NotNull(message = "상품 가격은 필수입니다.")
    @Min(value = 0, message = "상품 가격은 0원 이상이어야 합니다.")
    Integer price,

    @NotNull(message = "배송비는 필수입니다.")
    @Min(value = 0, message = "배송비는 0원 이상이어야 합니다.")
    Integer shippingFee
) {
}
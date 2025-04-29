package com.productmanagement.domain.productoptionvalue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductOptionValueCreateRequest(

    @NotBlank(message = "옵션 값은 필수입니다.")
    String value,

    @NotNull(message = "추가 금액은 필수입니다.")
    @Min(value = 0, message = "추가 금액은 0원 이상이어야 합니다.")
    Integer additionalPrice,

    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    Integer stock

) {
}
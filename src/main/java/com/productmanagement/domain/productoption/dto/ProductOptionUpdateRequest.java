package com.productmanagement.domain.productoption.dto;

import com.productmanagement.domain.productoption.entity.OptionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductOptionUpdateRequest(
    @NotBlank(message = "옵션 이름은 필수입니다.")
    String name,

    @NotNull(message = "옵션 타입은 필수입니다.")
    OptionType type,

    @NotNull(message = "추가 금액은 필수입니다.")
    @Min(value = 0, message = "추가 금액은 0 이상이어야 합니다.")
    Integer additionalPrice
) {
}
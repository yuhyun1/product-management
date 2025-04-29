package com.productmanagement.domain.productoption.dto;

import com.productmanagement.domain.productoption.entity.OptionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductOptionCreateRequest(

    @NotBlank(message = "옵션 이름은 필수입니다.")
    String name,

    @NotNull(message = "옵션 타입은 필수입니다.")
    OptionType type,

    List<String> values,  // SELECT 타입일 때만 사용, INPUT 이면 null/빈 배열 가능

    @NotNull(message = "추가 금액은 필수입니다.")
    @Min(value = 0, message = "추가 금액은 0원 이상이어야 합니다.")
    Integer additionalPrice

) {
}
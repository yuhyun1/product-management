package com.productmanagement.domain.productoption.controller;

import com.productmanagement.common.response.ApiResponse;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateRequest;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateResponse;
import com.productmanagement.domain.productoption.service.ProductOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 옵션 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    @Operation(summary = "상품 옵션 등록")
    @PostMapping("/products/{productId}/options")
    public ResponseEntity<ApiResponse<ProductOptionCreateResponse>> createProductOption(
        @PathVariable Long productId,
        @RequestBody @Valid ProductOptionCreateRequest request
    ) {
        ProductOptionCreateResponse response = productOptionService.createProductOption(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

}
package com.productmanagement.domain.productoption.controller;

import com.productmanagement.common.response.ApiResponse;
import com.productmanagement.domain.productoption.dto.*;
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
@RequestMapping("/api/products/{productId}/options")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    @Operation(summary = "상품 옵션 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductOptionCreateResponse>> createProductOption(
        @PathVariable Long productId,
        @RequestBody @Valid ProductOptionCreateRequest request
    ) {
        ProductOptionCreateResponse response = productOptionService.createProductOption(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 옵션 상세 조회")
    @GetMapping("/{optionId}")
    public ResponseEntity<ApiResponse<ProductOptionDetailResponse>> getProductOptionDetail(
        @PathVariable Long productId,
        @PathVariable Long optionId
    ) {
        ProductOptionDetailResponse response = productOptionService.getProductOptionDetail(productId, optionId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 옵션 수정")
    @PatchMapping("/{optionId}")
    public ResponseEntity<ApiResponse<ProductOptionUpdateResponse>> updateProductOption(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @RequestBody @Valid ProductOptionUpdateRequest request
    ) {
        ProductOptionUpdateResponse response = productOptionService.updateProductOption(productId, optionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

}
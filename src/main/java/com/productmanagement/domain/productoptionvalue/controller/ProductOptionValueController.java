package com.productmanagement.domain.productoptionvalue.controller;

import com.productmanagement.common.response.ApiResponse;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueCreateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateResponse;
import com.productmanagement.domain.productoptionvalue.service.ProductOptionValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 옵션값 관련 API")
@RestController
@RequestMapping("/api/products/{productId}/options")
@RequiredArgsConstructor
public class ProductOptionValueController {

    private final ProductOptionValueService productOptionValueService;

    @Operation(summary = "상품 옵션값 추가")
    @PostMapping("/{optionId}/values")
    public ResponseEntity<ApiResponse<ProductOptionValueResponse>> createProductOptionValue(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @RequestBody @Valid ProductOptionValueCreateRequest request
    ) {
        ProductOptionValueResponse response = productOptionValueService.createProductOptionValue(productId, optionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 옵션값 수정")
    @PatchMapping("/{optionId}/values/{valueId}")
    public ResponseEntity<ApiResponse<ProductOptionValueUpdateResponse>> updateProductOptionValue(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @PathVariable Long valueId,
        @RequestBody @Valid ProductOptionValueUpdateRequest request
    ) {
        ProductOptionValueUpdateResponse response = productOptionValueService.updateProductOptionValue(productId, optionId, valueId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 옵션값 삭제")
    @DeleteMapping("/{optionId}/values/{valueId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductOptionValue(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @PathVariable Long valueId
    ) {
        productOptionValueService.deleteProductOptionValue(productId, optionId, valueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

}

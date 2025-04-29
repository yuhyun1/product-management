package com.productmanagement.domain.product.controller;

import com.productmanagement.common.response.ApiResponse;
import com.productmanagement.domain.product.dto.ProductCreateRequest;
import com.productmanagement.domain.product.dto.ProductCreateResponse;
import com.productmanagement.domain.product.dto.ProductResponse;
import com.productmanagement.domain.product.dto.ProductUpdateRequest;
import com.productmanagement.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCreateResponse>> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        ProductCreateResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getProducts(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(products));
    }

    @Operation(summary = "상품 단건 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "상품 수정")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable Long productId,
        @RequestBody @Valid ProductUpdateRequest request
    ) {
        ProductResponse updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(updatedProduct));
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

}
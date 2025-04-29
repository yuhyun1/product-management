package com.productmanagement.domain.product.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.product.dto.ProductCreateRequest;
import com.productmanagement.domain.product.dto.ProductCreateResponse;
import com.productmanagement.domain.product.dto.ProductResponse;
import com.productmanagement.domain.product.dto.ProductUpdateRequest;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductQueryRepository;
import com.productmanagement.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;

    @Transactional
    public ProductCreateResponse createProduct(ProductCreateRequest request) {
        Product product = Product.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .shippingFee(request.shippingFee())
            .build();

        Product savedProduct = productRepository.save(product);

        return new ProductCreateResponse(savedProduct.getId());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productQueryRepository.findAllProducts(pageable);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        return productQueryRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(
            request.name(),
            request.description(),
            request.price(),
            request.shippingFee()
        );

        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getShippingFee(),
            product.getCreatedAt()
        );
    }



}
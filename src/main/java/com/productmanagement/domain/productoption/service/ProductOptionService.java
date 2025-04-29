package com.productmanagement.domain.productoption.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateRequest;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateResponse;
import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public ProductOptionCreateResponse createProductOption(Long productId, ProductOptionCreateRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        validateOptionLimit(product);
        validateOptionValuesByType(request);

        ProductOption option = ProductOption.builder()
            .product(product)
            .name(request.name())
            .type(request.type())
            .values(request.values() != null ? String.join(",", request.values()) : null)
            .additionalPrice(request.additionalPrice())
            .build();

        ProductOption savedOption = productOptionRepository.save(option);

        return new ProductOptionCreateResponse(
            savedOption.getId(),
            savedOption.getName(),
            savedOption.getType(),
            savedOption.getAdditionalPrice(),
            savedOption.getCreatedAt()
        );
    }


    private void validateOptionLimit(Product product) {
        long count = productOptionRepository.countByProductAndDeletedAtIsNull(product);
        if (count >= 3) {
            throw new CustomException(ErrorCode.PRODUCT_OPTION_LIMIT_EXCEEDED);
        }
    }

    private void validateOptionValuesByType(ProductOptionCreateRequest request) {
        if (request.type() == OptionType.SELECT) {
            if (request.values() == null || request.values().isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_OPTION_VALUES);
            }
        }
    }
}
package com.productmanagement.domain.productoption.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateRequest;
import com.productmanagement.domain.productoption.dto.ProductOptionCreateResponse;
import com.productmanagement.domain.productoption.dto.ProductOptionDetailResponse;
import com.productmanagement.domain.productoption.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.entity.ProductOptionValue;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoption.repository.ProductOptionValueQueryRepository;
import com.productmanagement.domain.productoption.repository.ProductOptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionValueQueryRepository productOptionValueQueryRepository;

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
            .additionalPrice(request.additionalPrice())
            .build();

        ProductOption savedOption = productOptionRepository.save(option);

        if (request.values() != null) {
            List<ProductOptionValue> values = request.values().stream()
                .map(valueRequest -> ProductOptionValue.builder()
                    .productOption(savedOption)
                    .value(valueRequest.value())
                    .additionalPrice(valueRequest.additionalPrice())
                    .stock(valueRequest.stock())
                    .build())
                .toList();

            productOptionValueRepository.saveAll(values);
        }

        return new ProductOptionCreateResponse(
            savedOption.getId(),
            savedOption.getName(),
            savedOption.getType(),
            savedOption.getAdditionalPrice(),
            savedOption.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ProductOptionDetailResponse getProductOptionDetail(Long productId, Long optionId) {
        ProductOption option = productOptionRepository.findByIdAndDeletedAtIsNull(optionId)
            .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        validateProductMatch(productId, option);

        List<ProductOptionValueResponse> values = productOptionValueQueryRepository.findAllByOptionId(optionId);

        return new ProductOptionDetailResponse(
            option.getId(),
            option.getName(),
            option.getType(),
            option.getAdditionalPrice(),
            values
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

    private static void validateProductMatch(Long productId, ProductOption option) {
        if (!option.getProduct().getId().equals(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_OPTION_MISMATCH);
        }
    }
}
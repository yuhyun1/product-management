package com.productmanagement.domain.productoptionvalue.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueCreateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateResponse;
import com.productmanagement.domain.productoptionvalue.entity.ProductOptionValue;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductOptionValueService {

    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;

    @Transactional
    public ProductOptionValueResponse createProductOptionValue(
        Long productId,
        Long optionId,
        ProductOptionValueCreateRequest request
    ) {
        ProductOption option = getProductOption(optionId);
        validateProductMatch(productId, option);

        ProductOptionValue value = ProductOptionValue.builder()
            .productOption(option)
            .value(request.value())
            .additionalPrice(request.additionalPrice())
            .stock(request.stock())
            .build();

        ProductOptionValue savedValue = productOptionValueRepository.save(value);

        return new ProductOptionValueResponse(
            savedValue.getId(),
            savedValue.getValue(),
            savedValue.getAdditionalPrice(),
            savedValue.getStock()
        );
    }

    @Transactional
    public ProductOptionValueUpdateResponse updateProductOptionValue(
        Long productId,
        Long optionId,
        Long valueId,
        ProductOptionValueUpdateRequest request
    ) {
        ProductOption option = getProductOption(optionId);
        validateProductMatch(productId, option);

        ProductOptionValue value = getProductOptionValue(valueId);
        validateProductOptionMatch(optionId, value);

        value.updateOptionValue(request.value(), request.additionalPrice(), request.stock());

        return new ProductOptionValueUpdateResponse(
            value.getId(),
            value.getValue(),
            value.getAdditionalPrice(),
            value.getStock(),
            value.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteProductOptionValue(Long productId, Long optionId, Long valueId) {
        ProductOption option = getProductOption(optionId);
        validateProductMatch(productId, option);

        ProductOptionValue value = getProductOptionValue(valueId);
        validateProductOptionMatch(optionId, value);

        value.softDelete();
    }


    private ProductOptionValue getProductOptionValue(Long valueId) {
        return productOptionValueRepository.findByIdAndDeletedAtIsNull(valueId)
            .orElseThrow(() -> new CustomException(ErrorCode.OPTION_VALUE_NOT_FOUND));
    }

    private ProductOption getProductOption(Long optionId) {
        return productOptionRepository.findByIdAndDeletedAtIsNull(optionId)
            .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));
    }

    private static void validateProductMatch(Long productId, ProductOption option) {
        if (!option.getProduct().getId().equals(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_OPTION_MISMATCH);
        }
    }

    private static void validateProductOptionMatch(Long optionId, ProductOptionValue value) {
        if (!value.getProductOption().getId().equals(optionId)) {
            throw new CustomException(ErrorCode.OPTION_VALUE_MISMATCH);
        }
    }
}

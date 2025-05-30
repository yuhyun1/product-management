package com.productmanagement.domain.productoption.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.*;
import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoptionvalue.entity.ProductOptionValue;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueQueryRepository;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueRepository;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionValueQueryRepository productOptionValueQueryRepository;

    @Transactional
    public ProductOptionCreateResponse createProductOption(
        Long productId,
        ProductOptionCreateRequest request,
        Long currentMemberId
    ) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        validateProductOwner(product, currentMemberId);
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

        log.info("[상품 옵션 등록 완료] optionId={}, name={}, productId={}",
            savedOption.getId(), savedOption.getName(), productId);

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
        ProductOption option = getProductOption(optionId);
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

    @Transactional
    public ProductOptionUpdateResponse updateProductOption(Long productId, Long optionId, ProductOptionUpdateRequest request, Long currentMemberId) {
        ProductOption option = getProductOption(optionId);
        validateProductOwner(option.getProduct(), currentMemberId);
        validateProductMatch(productId, option);

        option.updateOption(request.name(), request.type(), request.additionalPrice());

        log.info("[상품 옵션 수정 완료] optionId={}, name={}, productId={}",
            option.getId(), option.getName(), productId);

        return new ProductOptionUpdateResponse(
            option.getId(),
            option.getName(),
            option.getType(),
            option.getAdditionalPrice(),
            option.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteProductOption(Long productId, Long optionId, Long currentMemberId) {
        ProductOption option = getProductOption(optionId);
        validateProductOwner(option.getProduct(), currentMemberId);
        validateProductMatch(productId, option);

        option.softDelete();
        productOptionValueQueryRepository.bulkSoftDeleteByOptionId(optionId);

        log.warn("[상품 옵션 삭제 처리] optionId={}, name={}, productId={}",
            option.getId(), option.getName(), productId);
    }


    private void validateOptionLimit(Product product) {
        long count = productOptionRepository.countByProductAndDeletedAtIsNull(product);
        if (count >= 3) {
            throw new CustomException(ErrorCode.PRODUCT_OPTION_LIMIT_EXCEEDED);
        }
    }

    private void validateOptionValuesByType(ProductOptionCreateRequest request) {
        OptionType type = request.type();
        List<ProductOptionValueCreateRequest> values = request.values();

        if (type == OptionType.INPUT) {
            if (values != null && !values.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_OPTION_VALUE);
            }
        }

        if (type == OptionType.SELECT) {
            if (values == null || values.isEmpty()) {
                throw new CustomException(ErrorCode.OPTION_VALUE_REQUIRED);
            }
        }
    }

    private static void validateProductMatch(Long productId, ProductOption option) {
        if (!option.getProduct().getId().equals(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_OPTION_MISMATCH);
        }
    }

    private void validateProductOwner(Product product, Long currentMemberId) {
        if (!product.getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
    }

    private ProductOption getProductOption(Long optionId) {
        return productOptionRepository.findByIdAndDeletedAtIsNull(optionId)
            .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));
    }
}
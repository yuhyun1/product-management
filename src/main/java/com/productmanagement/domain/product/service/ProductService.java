package com.productmanagement.domain.product.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.common.exception.ErrorCode;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.member.repository.MemberRepository;
import com.productmanagement.domain.product.dto.*;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductQueryRepository;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.ProductOptionSummaryResponse;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionQueryRepository;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductOptionQueryRepository productOptionQueryRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueQueryRepository productOptionValueQueryRepository;

    @Transactional
    public ProductCreateResponse createProduct(ProductCreateRequest request, Long currentMemberId) {
        Member member = memberRepository.findById(currentMemberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Product product = Product.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .stock(request.stock())
            .shippingFee(request.shippingFee())
            .member(member)
            .build();

        Product savedProduct = productRepository.save(product);

        log.info("[상품 등록 완료] id={}, name={}", savedProduct.getId(), savedProduct.getName());
        return new ProductCreateResponse(savedProduct.getId());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productQueryRepository.findAllProducts(pageable);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = getProduct(productId);

        List<ProductOptionSummaryResponse> productOptions =
            productOptionQueryRepository.findSummaryByProductId(product.getId());

        return new ProductDetailResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getShippingFee(),
            product.getStock(),
            product.getCreatedAt(),
            productOptions
        );
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, Long currentMemberId) {
        Product product = getProduct(productId);
        validateProductOwner(product, currentMemberId);

        product.update(
            request.name(),
            request.description(),
            request.price(),
            request.stock(),
            request.shippingFee()
        );

        log.info("[상품 수정 완료] id={}, name={}", product.getId(), product.getName());
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getShippingFee(),
            product.getCreatedAt()
        );
    }

    @Transactional
    public void deleteProduct(Long productId, Long currentMemberId) {
        Product product = getProduct(productId);
        validateProductOwner(product, currentMemberId);
        product.softDelete();

        List<ProductOption> options = productOptionRepository.findAllByProductAndDeletedAtIsNull(product);

        for (ProductOption option : options) {
            option.softDelete();
            productOptionValueQueryRepository.bulkSoftDeleteByOptionId(option.getId());
        }

        log.warn("[상품 삭제 처리] id={}, name={}", product.getId(), product.getName());
    }

    private void validateProductOwner(Product product, Long currentMemberId) {
        if (!product.getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

}
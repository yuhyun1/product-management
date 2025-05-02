package com.productmanagement.domain.productoptionvalue.service;

import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueCreateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueUpdateResponse;
import com.productmanagement.domain.productoptionvalue.entity.ProductOptionValue;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
class ProductOptionValueServiceTest {

    @InjectMocks
    private ProductOptionValueService productOptionValueService;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    private final Long productId = 1L;
    private final Long optionId = 2L;
    private final Long valueId = 3L;
    private final Long memberId = 10L;

    @Test
    void 옵션값_등록_성공() {
        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();
        ProductOption option = ProductOption.builder().id(optionId).product(product).build();

        ProductOptionValueCreateRequest request = new ProductOptionValueCreateRequest("빨강", 500, 10);
        ProductOptionValue savedValue = ProductOptionValue.builder()
            .id(100L)
            .productOption(option)
            .value("빨강")
            .additionalPrice(500)
            .stock(10)
            .build();

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));
        given(productOptionValueRepository.save(any(ProductOptionValue.class))).willReturn(savedValue);

        ProductOptionValueResponse response =
            productOptionValueService.createProductOptionValue(productId, optionId, request, memberId);

        assertThat(response.value()).isEqualTo("빨강");
        assertThat(response.additionalPrice()).isEqualTo(500);
        assertThat(response.stock()).isEqualTo(10);
    }

    @Test
    void 옵션값_수정_성공() {
        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();
        ProductOption option = ProductOption.builder().id(optionId).product(product).build();
        ProductOptionValue value = ProductOptionValue.builder()
            .id(valueId)
            .productOption(option)
            .value("빨강")
            .additionalPrice(500)
            .stock(10)
            .build();

        ProductOptionValueUpdateRequest request = new ProductOptionValueUpdateRequest("파랑", 1000, 20);

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));
        given(productOptionValueRepository.findByIdAndDeletedAtIsNull(valueId)).willReturn(Optional.of(value));

        ProductOptionValueUpdateResponse response =
            productOptionValueService.updateProductOptionValue(productId, optionId, valueId, request, memberId);

        assertThat(response.value()).isEqualTo("파랑");
        assertThat(response.additionalPrice()).isEqualTo(1000);
        assertThat(response.stock()).isEqualTo(20);
    }

    @Test
    void 옵션값_삭제_성공() {
        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();
        ProductOption option = ProductOption.builder().id(optionId).product(product).build();
        ProductOptionValue value = ProductOptionValue.builder().id(valueId).productOption(option).build();

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));
        given(productOptionValueRepository.findByIdAndDeletedAtIsNull(valueId)).willReturn(Optional.of(value));

        productOptionValueService.deleteProductOptionValue(productId, optionId, valueId, memberId);

        assertThat(value.getDeletedAt()).isNotNull();
    }
}

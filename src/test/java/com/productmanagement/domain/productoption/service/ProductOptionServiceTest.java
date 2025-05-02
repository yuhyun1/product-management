package com.productmanagement.domain.productoption.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.*;
import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueCreateRequest;
import com.productmanagement.domain.productoptionvalue.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueQueryRepository;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductOptionServiceTest {

    @InjectMocks
    private ProductOptionService productOptionService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    @Mock
    private ProductOptionValueQueryRepository productOptionValueQueryRepository;

    @Test
    void 상품옵션_등록_성공_SELECT타입() {
        // given
        Long productId = 1L;
        Long memberId = 10L;

        Member member = Member.builder().id(memberId).build();

        Product product = Product.builder()
            .id(productId)
            .member(member)
            .build();

        List<ProductOptionValueCreateRequest> values = List.of(
            new ProductOptionValueCreateRequest("빨강", 0, 10),
            new ProductOptionValueCreateRequest("파랑", 1000, 5)
        );

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "색상",
            OptionType.SELECT,
            0,
            values
        );

        ProductOption savedOption = ProductOption.builder()
            .id(100L)
            .name("색상")
            .type(OptionType.SELECT)
            .additionalPrice(0)
            .product(product)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.save(any())).willReturn(savedOption);

        // when
        ProductOptionCreateResponse response = productOptionService.createProductOption(productId, request, memberId);

        // then
        assertThat(response.name()).isEqualTo("색상");
        assertThat(response.type()).isEqualTo(OptionType.SELECT);
        verify(productOptionValueRepository, times(1)).saveAll(any());
    }

    @Test
    void 상품옵션_등록_성공_INPUT타입() {
        // given
        Long productId = 1L;
        Long memberId = 10L;

        Member member = Member.builder().id(memberId).build();

        Product product = Product.builder()
            .id(productId)
            .member(member)
            .build();

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "문구 입력",
            OptionType.INPUT,
            1500,
            null
        );

        ProductOption savedOption = ProductOption.builder()
            .id(200L)
            .name("문구 입력")
            .type(OptionType.INPUT)
            .additionalPrice(1500)
            .product(product)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.save(any())).willReturn(savedOption);

        // when
        ProductOptionCreateResponse response = productOptionService.createProductOption(productId, request, memberId);

        // then
        assertThat(response.name()).isEqualTo("문구 입력");
        assertThat(response.type()).isEqualTo(OptionType.INPUT);

        verify(productOptionValueRepository, never()).saveAll(any());
    }

    @Test
    void 상품옵션_등록_실패_상품없음() {
        // given
        Long productId = 1L;
        Long memberId = 10L;

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "색상",
            OptionType.SELECT,
            0,
            List.of(new ProductOptionValueCreateRequest("빨강", 0, 10))
        );

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // expect
        Throwable exception = catchThrowable(() -> productOptionService.createProductOption(productId, request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    void 상품옵션_등록_실패_상품권한없음() {
        Long productId = 1L;
        Long memberId = 10L;

        Member otherMember = Member.builder().id(99L).build();
        Product product = Product.builder().id(productId).member(otherMember).build();

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "색상",
            OptionType.SELECT,
            0,
            List.of(new ProductOptionValueCreateRequest("빨강", 0, 10))
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        Throwable exception = catchThrowable(() -> productOptionService.createProductOption(productId, request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("권한이 없습니다.");
    }

    @Test
    void 상품옵션_등록_실패_옵션3개초과() {
        Long productId = 1L;
        Long memberId = 10L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "색상",
            OptionType.SELECT,
            0,
            List.of(new ProductOptionValueCreateRequest("빨강", 0, 10))
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.countByProductAndDeletedAtIsNull(product)).willReturn(3L);

        Throwable exception = catchThrowable(() -> productOptionService.createProductOption(productId, request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("상품 옵션은 최대 3개까지만 등록할 수 있습니다.");
    }

    @Test
    void 상품옵션_등록_실패_SELECT타입옵션값없음() {
        Long productId = 1L;
        Long memberId = 10L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "색상",
            OptionType.SELECT,
            0,
            null
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.countByProductAndDeletedAtIsNull(product)).willReturn(0L);

        Throwable exception = catchThrowable(() -> productOptionService.createProductOption(productId, request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("선택형 옵션은 값 목록을 필수로 입력해야 합니다.");
    }

    @Test
    void 상품옵션_등록_실패_INPUT타입옵션값있음() {
        Long productId = 1L;
        Long memberId = 10L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();

        ProductOptionCreateRequest request = new ProductOptionCreateRequest(
            "문구 입력",
            OptionType.INPUT,
            1000,
            List.of(new ProductOptionValueCreateRequest("임의값", 0, 1))
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.countByProductAndDeletedAtIsNull(product)).willReturn(0L);

        Throwable exception = catchThrowable(() -> productOptionService.createProductOption(productId, request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("입력형 옵션은 값 목록을 저장할 수 없습니다.");
    }

    @Test
    void 상품옵션_단건조회_성공() {
        // given
        Long productId = 1L;
        Long optionId = 10L;

        Product product = Product.builder().id(productId).build();
        ProductOption option = ProductOption.builder()
            .id(optionId)
            .name("색상")
            .type(OptionType.SELECT)
            .additionalPrice(1000)
            .product(product)
            .build();

        List<ProductOptionValueResponse> valueResponses = List.of(
            new ProductOptionValueResponse(1L, "빨강", 0, 10),
            new ProductOptionValueResponse(2L, "파랑", 500, 5)
        );

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));
        given(productOptionValueQueryRepository.findAllByOptionId(optionId)).willReturn(valueResponses);

        // when
        ProductOptionDetailResponse response = productOptionService.getProductOptionDetail(productId, optionId);

        // then
        assertThat(response.name()).isEqualTo("색상");
        assertThat(response.values()).hasSize(2);
    }

    @Test
    void 상품옵션_수정_성공() {
        // given
        Long productId = 1L;
        Long optionId = 10L;
        Long memberId = 100L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();
        ProductOption option = ProductOption.builder()
            .id(optionId)
            .name("색상")
            .type(OptionType.SELECT)
            .additionalPrice(0)
            .product(product)
            .build();

        ProductOptionUpdateRequest updateRequest = new ProductOptionUpdateRequest(
            "컬러",
            OptionType.SELECT,
            500
        );

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));

        // when
        ProductOptionUpdateResponse response =
            productOptionService.updateProductOption(productId, optionId, updateRequest, memberId);

        // then
        assertThat(response.name()).isEqualTo("컬러");
        assertThat(response.additionalPrice()).isEqualTo(500);
    }

    @Test
    void 상품옵션_삭제_성공() {
        // given
        Long productId = 1L;
        Long optionId = 10L;
        Long memberId = 100L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(productId).member(member).build();
        ProductOption option = ProductOption.builder()
            .id(optionId)
            .name("크기")
            .product(product)
            .build();

        given(productOptionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));

        // when
        productOptionService.deleteProductOption(productId, optionId, memberId);

        // then
        verify(productOptionValueQueryRepository).bulkSoftDeleteByOptionId(optionId);
        assertThat(option.getDeletedAt()).isNotNull();
    }


}

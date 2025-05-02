package com.productmanagement.domain.product.service;

import com.productmanagement.common.exception.CustomException;
import com.productmanagement.domain.member.entity.Member;
import com.productmanagement.domain.member.repository.MemberRepository;
import com.productmanagement.domain.product.dto.*;
import com.productmanagement.domain.product.entity.Product;
import com.productmanagement.domain.product.repository.ProductRepository;
import com.productmanagement.domain.productoption.dto.ProductOptionSummaryResponse;
import com.productmanagement.domain.productoption.entity.OptionType;
import com.productmanagement.domain.productoption.entity.ProductOption;
import com.productmanagement.domain.productoption.repository.ProductOptionQueryRepository;
import com.productmanagement.domain.productoptionvalue.repository.ProductOptionValueQueryRepository;
import com.productmanagement.domain.product.repository.ProductQueryRepository;
import com.productmanagement.domain.productoption.repository.ProductOptionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock private ProductRepository productRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProductQueryRepository productQueryRepository;
    @Mock private ProductOptionQueryRepository productOptionQueryRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueQueryRepository productOptionValueQueryRepository;

    @Test
    void 상품_등록_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
            .id(memberId)
            .email("test@user.com")
            .password("encoded")
            .build();

        ProductCreateRequest request = new ProductCreateRequest("상품명", "설명", 10000, 100, 3000);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.save(any(Product.class)))
            .willAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return Product.builder()
                    .id(1L)
                    .name(p.getName())
                    .description(p.getDescription())
                    .price(p.getPrice())
                    .stock(p.getStock())
                    .shippingFee(p.getShippingFee())
                    .member(p.getMember())
                    .build();
            });

        // when
        ProductCreateResponse response = productService.createProduct(request, memberId);

        // then
        assertThat(response.productId()).isEqualTo(1L);
    }

    @Test
    void 상품_등록_실패_회원없음() {
        // given
        Long memberId = 1L;
        ProductCreateRequest request = new ProductCreateRequest("상품명", "설명", 1000, 10, 3000);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // expect
        Throwable exception = catchThrowable(() -> productService.createProduct(request, memberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 상품_수정_성공() {
        // given
        Long memberId = 1L;
        Long productId = 10L;

        Member member = Member.builder()
            .id(memberId)
            .email("user@email.com")
            .build();

        Product product = Product.builder()
            .id(productId)
            .name("old name")
            .description("old description")
            .price(1000)
            .stock(10)
            .shippingFee(3000)
            .member(member)
            .build();

        ProductUpdateRequest request =
            new ProductUpdateRequest("new name", "new description", 1500, 20, 3500);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.updateProduct(productId, request, memberId);

        // then
        assertThat(response.name()).isEqualTo("new name");
        assertThat(response.price()).isEqualTo(1500);
    }

    @Test
    void 상품_수정_실패_작성자아님() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Long productId = 10L;

        Member owner = Member.builder()
            .id(ownerId)
            .email("owner@email.com")
            .build();

        Product product = Product.builder()
            .id(productId)
            .name("상품")
            .description("설명")
            .price(1000)
            .stock(10)
            .shippingFee(3000)
            .member(owner)
            .build();

        ProductUpdateRequest request = new ProductUpdateRequest("수정이름", "수정설명", 1500, 20, 3500);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // expect
        Throwable exception = catchThrowable(() -> productService.updateProduct(productId, request, otherUserId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("권한이 없습니다.");
    }

    @Test
    void 상품_삭제_성공() {
        // given
        Long memberId = 1L;
        Long productId = 100L;

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder()
            .id(productId)
            .name("상품")
            .member(member)
            .build();

        ProductOption option1 = ProductOption.builder().id(1L).product(product).build();
        ProductOption option2 = ProductOption.builder().id(2L).product(product).build();
        List<ProductOption> options = List.of(option1, option2);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionRepository.findAllByProductAndDeletedAtIsNull(product)).willReturn(options);

        // when
        productService.deleteProduct(productId, memberId);

        // then
        assertThat(product.getDeletedAt()).isNotNull();
        assertThat(option1.getDeletedAt()).isNotNull();
        assertThat(option2.getDeletedAt()).isNotNull();

        verify(productOptionValueQueryRepository, times(1)).bulkSoftDeleteByOptionId(option1.getId());
        verify(productOptionValueQueryRepository, times(1)).bulkSoftDeleteByOptionId(option2.getId());
    }

    @Test
    void 상품_삭제_실패_작성자아님() {
        // given
        Long productId = 1L;
        Long currentMemberId = 2L;  // 작성자가 아닌 사용자

        Member owner = Member.builder().id(1L).build();
        Product product = Product.builder().id(productId).member(owner).build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // expect
        Throwable exception = catchThrowable(() -> productService.deleteProduct(productId, currentMemberId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("권한이 없습니다.");
    }

    @Test
    void 상품_상세조회_성공() {
        // given
        Long productId = 1L;

        Product product = Product.builder()
            .id(productId)
            .name("상품명")
            .description("설명")
            .price(1000)
            .stock(10)
            .shippingFee(2500)
            .build();

        List<ProductOptionSummaryResponse> options = List.of(
            new ProductOptionSummaryResponse(1L, "색상", OptionType.SELECT, 0),
            new ProductOptionSummaryResponse(2L, "사이즈", OptionType.INPUT, 1000)
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productOptionQueryRepository.findSummaryByProductId(productId)).willReturn(options);

        // when
        ProductDetailResponse response = productService.getProductDetail(productId);

        // then
        assertThat(response.name()).isEqualTo("상품명");
        assertThat(response.options()).hasSize(2);
    }

    @Test
    void 상품_상세조회_실패_존재하지않음() {
        // given
        Long productId = 99L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // expect
        Throwable exception = catchThrowable(() -> productService.getProductDetail(productId));

        assertThat(exception)
            .isInstanceOf(CustomException.class)
            .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    void 상품_목록_페이징조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        ProductResponse product1 =
            new ProductResponse(1L, "상품1", "설명1", 1000, 10, 3000, LocalDateTime.now());
        ProductResponse product2 =
            new ProductResponse(2L, "상품2", "설명2", 2000, 5, 2500, LocalDateTime.now());

        Page<ProductResponse> page = new PageImpl<>(List.of(product1, product2), pageable, 2);

        given(productQueryRepository.findAllProducts(pageable)).willReturn(page);

        // when
        Page<ProductResponse> response = productService.getProducts(pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().getFirst().name()).isEqualTo("상품1");
    }

}

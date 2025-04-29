package com.productmanagement.domain.product.repository;

import com.productmanagement.domain.product.dto.ProductResponse;
import com.productmanagement.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    QProduct product = QProduct.product;

    @Override
    public Page<ProductResponse> findAllProducts(Pageable pageable) {
        List<ProductResponse> content = queryFactory
            .select(
                Projections.constructor(
                    ProductResponse.class,
                    product.id,
                    product.name,
                    product.description,
                    product.price,
                    product.shippingFee,
                    product.createdAt
                ))
            .from(product)
            .where(product.isDeleted.isFalse())
            .orderBy(product.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long count = queryFactory
            .select(product.count())
            .from(product)
            .where(product.isDeleted.isFalse())
            .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    @Override
    public Optional<ProductResponse> findById(Long productId) {
        ProductResponse result = queryFactory
            .select(Projections.constructor(
                ProductResponse.class,
                product.id,
                product.name,
                product.description,
                product.price,
                product.shippingFee,
                product.createdAt
            ))
            .from(product)
            .where(
                product.isDeleted.isFalse(),
                product.id.eq(productId)
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

}

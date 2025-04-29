package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.dto.ProductOptionSummaryResponse;
import com.productmanagement.domain.productoption.entity.QProductOption;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOptionQueryRepositoryImpl implements ProductOptionQueryRepository {

    private final JPAQueryFactory queryFactory;

    QProductOption productOption = QProductOption.productOption;

    @Override
    public List<ProductOptionSummaryResponse> findSummaryByProductId(Long productId) {
        return queryFactory
            .select(Projections.constructor(
                ProductOptionSummaryResponse.class,
                productOption.id,
                productOption.name,
                productOption.type,
                productOption.additionalPrice
            ))
            .from(productOption)
            .where(
                productOption.product.id.eq(productId),
                productOption.deletedAt.isNull()
            )
            .orderBy(productOption.createdAt.asc())
            .fetch();
    }
}
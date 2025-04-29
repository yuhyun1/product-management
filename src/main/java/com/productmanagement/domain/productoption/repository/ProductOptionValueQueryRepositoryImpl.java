package com.productmanagement.domain.productoption.repository;

import com.productmanagement.domain.productoption.dto.ProductOptionValueResponse;
import com.productmanagement.domain.productoption.entity.QProductOptionValue;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOptionValueQueryRepositoryImpl implements ProductOptionValueQueryRepository {

    private final JPAQueryFactory queryFactory;

    QProductOptionValue optionValue = QProductOptionValue.productOptionValue;

    @Override
    public List<ProductOptionValueResponse> findAllByOptionId(Long optionId) {
        return queryFactory
            .select(Projections.constructor(
                ProductOptionValueResponse.class,
                optionValue.id,
                optionValue.value,
                optionValue.additionalPrice,
                optionValue.stock
            ))
            .from(optionValue)
            .where(
                optionValue.productOption.id.in(optionId),
                optionValue.deletedAt.isNull()
            )
            .orderBy(optionValue.createdAt.asc())
            .fetch();
    }

    @Override
    @Transactional
    public void bulkSoftDeleteByOptionId(Long optionId) {
        queryFactory
            .update(optionValue)
            .set(optionValue.deletedAt, LocalDateTime.now())
            .where(
                optionValue.productOption.id.eq(optionId),
                optionValue.deletedAt.isNull()
            )
            .execute();
    }
}
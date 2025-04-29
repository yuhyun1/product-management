package com.productmanagement.domain.productoptionvalue.entity;

import com.productmanagement.common.entity.BaseTimeEntity;
import com.productmanagement.domain.productoption.entity.ProductOption;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductOptionValue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(nullable = false, length = 100)
    private String value;

    @Column(nullable = false)
    private Integer additionalPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateOptionValue(String value, Integer additionalPrice, Integer stock) {
        this.value = value;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }


}

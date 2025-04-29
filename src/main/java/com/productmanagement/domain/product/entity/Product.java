package com.productmanagement.domain.product.entity;

import com.productmanagement.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int shippingFee;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update(String name, String description, int price, int stock, int shippingFee) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.shippingFee = shippingFee;
    }

}
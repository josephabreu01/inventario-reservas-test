package com.inventrio.product.infrastructure.adapter.out.persistence;

import com.inventrio.product.domain.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String category;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name = "current_stock", nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    public static ProductEntity fromDomain(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .sku(product.getSku())
                .currentStock(product.getCurrentStock())
                .build();
    }

    public Product toDomain() {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .sku(this.sku)
                .currentStock(this.currentStock)
                .build();
    }
}

package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import com.inventrio.inventory.domain.model.Inventory;
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
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_price")
    private BigDecimal price;

    @Column(name = "product_category")
    private String category;

    public static InventoryEntity fromDomain(Inventory domain) {
        Objects.requireNonNull(domain, "Inventory cannot be null");
        return InventoryEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .quantity(domain.getQuantity())
                .name(domain.getName())
                .price(domain.getPrice())
                .category(domain.getCategory())
                .build();
    }

    public Inventory toDomain() {
        return Inventory.builder()
                .id(this.id)
                .productId(this.productId)
                .quantity(this.quantity)
                .name(this.name)
                .price(this.price)
                .category(this.category)
                .build();
    }
}

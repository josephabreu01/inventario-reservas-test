package com.inventrio.product.infrastructure.adapter.out.persistence;

import com.inventrio.product.domain.model.PriceHistory;
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
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public static PriceHistoryEntity fromDomain(PriceHistory history) {
        Objects.requireNonNull(history, "PriceHistory cannot be null");
        return PriceHistoryEntity.builder()
                .id(history.getId())
                .productId(history.getProductId())
                .price(history.getPrice())
                .changedAt(history.getChangedAt())
                .build();
    }

    public PriceHistory toDomain() {
        return PriceHistory.builder()
                .id(this.id)
                .productId(this.productId)
                .price(this.price)
                .changedAt(this.changedAt)
                .build();
    }
}

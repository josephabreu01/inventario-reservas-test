package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "inventory_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 10)
    private MovementType movementType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static InventoryMovementEntity fromDomain(InventoryMovement domain) {
        Objects.requireNonNull(domain, "InventoryMovement cannot be null");
        return InventoryMovementEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .quantityChange(domain.getQuantityChange())
                .movementType(domain.getMovementType())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public InventoryMovement toDomain() {
        return InventoryMovement.builder()
                .id(this.id)
                .productId(this.productId)
                .quantityChange(this.quantityChange)
                .movementType(this.movementType)
                .createdAt(this.createdAt)
                .build();
    }
}

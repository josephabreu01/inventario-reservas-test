package com.inventrio.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement {
    private Long id;
    private Long productId;
    private Integer quantityChange;
    private MovementType movementType;
    private LocalDateTime createdAt;
}

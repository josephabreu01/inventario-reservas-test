package com.inventrio.inventory.domain.model;

import com.inventrio.inventory.domain.exception.BadRequestException;

public enum MovementType {
    ENTRY, EXIT;

    public static MovementType fromString(String value) {
        try {
            return MovementType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid movement type: " + value + ". Must be ENTRY or EXIT");
        }
    }
}

package com.inventrio.inventory.infrastructure.adapter.in.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class InventoryResourceTest {

    @InjectMock
    InventoryUseCase inventoryUseCase;

    @Test
    @TestSecurity(user = "user", roles = {"User"})
    void testGetStock_Success() {
        Inventory inv = Inventory.builder().id(1L).productId(100L).quantity(25).build();
        when(inventoryUseCase.getStock(100L)).thenReturn(inv);

        given()
                .when().get("/api/inventory/100")
                .then()
                .statusCode(200)
                .body("productId", is(100))
                .body("quantity", is(25));
    }

    @Test
    @TestSecurity(user = "user", roles = {"User"})
    void testAdjustStock_ForbiddenForUser() {
        given()
                .contentType("application/json")
                .body("{\"productId\":100,\"quantity\":5,\"type\":\"ENTRY\"}")
                .when().post("/api/inventory/adjust")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"Admin"})
    void testAdjustStock_SuccessForAdmin() {
        Inventory updated = Inventory.builder().id(1L).productId(100L).quantity(30).build();
        when(inventoryUseCase.adjustStock(100L, 5, MovementType.ENTRY)).thenReturn(updated);

        given()
                .contentType("application/json")
                .body("{\"productId\":100,\"quantity\":5,\"type\":\"ENTRY\"}")
                .when().post("/api/inventory/adjust")
                .then()
                .statusCode(200)
                .body("productId", is(100))
                .body("quantity", is(30));
    }

    @Test
    @TestSecurity(user = "user", roles = {"User"})
    void testGetMovements_Success() {
        InventoryMovement movement = new InventoryMovement(1L, 100L, 5, MovementType.ENTRY, LocalDateTime.now());
        when(inventoryUseCase.getMovements(anyLong(), anyInt(), anyInt())).thenReturn(List.of(movement));

        given()
                .when().get("/api/inventory/100/movements")
                .then()
                .statusCode(200)
                .body("[0].productId", is(100))
                .body("[0].quantityChange", is(5))
                .body("[0].movementType", is("ENTRY"));
    }
}

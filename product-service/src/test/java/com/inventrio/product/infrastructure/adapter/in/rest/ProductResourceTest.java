package com.inventrio.product.infrastructure.adapter.in.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.in.ProductUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductResourceTest {

    @InjectMock
    ProductUseCase productUseCase;

    @Test
    @TestSecurity(user = "user", roles = {"User"})
    void testGetAllProducts_Success() {
        Product p = Product.builder().id(1L).name("Test Product").description("Desc")
                .price(BigDecimal.TEN).category("Cat").sku("SKU1").build();
        when(productUseCase.getAllProducts(anyInt(), anyInt(), any())).thenReturn(List.of(p));

        given()
                .when().get("/api/products")
                .then()
                .statusCode(200)
                .body("[0].name", is("Test Product"))
                .body("[0].sku", is("SKU1"));
    }

    @Test
    @TestSecurity(user = "user", roles = {"User"})
    void testCreateProduct_ForbiddenForUser() {
        given()
                .contentType("application/json")
                .body("{\"name\":\"P\",\"description\":\"Desc\",\"price\":10,\"category\":\"Cat\",\"sku\":\"S\"}")
                .when().post("/api/products")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"Admin"})
    void testCreateProduct_SuccessForAdmin() {
        Product created = Product.builder().id(1L).name("P").description("Desc")
                .price(BigDecimal.TEN).category("Cat").sku("SKU1").build();
        when(productUseCase.createProduct(any())).thenReturn(created);

        given()
                .contentType("application/json")
                .body("{\"name\":\"P\",\"description\":\"Desc\",\"price\":10,\"category\":\"Cat\",\"sku\":\"SKU1\"}")
                .when().post("/api/products")
                .then()
                .statusCode(201)
                .body("id", is(1))
                .body("sku", is("SKU1"));
    }
}

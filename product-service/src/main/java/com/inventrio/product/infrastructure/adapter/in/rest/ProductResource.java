package com.inventrio.product.infrastructure.adapter.in.rest;

import com.inventrio.product.application.ProductApplicationService;
import com.inventrio.product.application.ProductRequest;
import com.inventrio.product.application.ProductResponse;
import com.inventrio.product.application.PriceHistoryResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductApplicationService productAppService;

    @Inject
    public ProductResource(ProductApplicationService productAppService) {
        this.productAppService = productAppService;
    }

    @POST
    @RolesAllowed("Admin")
    public Response createProduct(@Valid ProductRequest request) {
        ProductResponse created = productAppService.createProduct(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @RolesAllowed({"Admin", "User"})
    public Response getAllProducts(
            @QueryParam("category") String category,
            @QueryParam("currency") String currency,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<ProductResponse> response = productAppService.getAllProducts(category, page, size, currency);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"Admin", "User"})
    public Response getProductById(
            @PathParam("id") Long id,
            @QueryParam("currency") String currency) {
        return Response.ok(productAppService.getProduct(id, currency)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("Admin")
    public Response updateProduct(@PathParam("id") Long id, @Valid ProductRequest request) {
        return Response.ok(productAppService.updateProduct(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("Admin")
    public Response deleteProduct(@PathParam("id") Long id) {
        productAppService.deleteProduct(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/stock")
    @RolesAllowed({"Admin", "User"})
    public Response getStock(@PathParam("id") Long id) {
        return Response.ok(productAppService.getProductStock(id)).build();
    }

    @GET
    @Path("/{id}/price-history")
    @RolesAllowed({"Admin", "User"})
    public Response getPriceHistory(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<PriceHistoryResponse> history = productAppService.getPriceHistory(id, page, size);
        return Response.ok(history).build();
    }
}

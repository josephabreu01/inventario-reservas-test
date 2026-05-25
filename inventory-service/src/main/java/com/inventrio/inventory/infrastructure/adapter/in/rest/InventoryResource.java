package com.inventrio.inventory.infrastructure.adapter.in.rest;

import com.inventrio.inventory.application.AdjustStockRequest;
import com.inventrio.inventory.application.InventoryApplicationService;
import com.inventrio.inventory.application.InventoryMovementResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import java.util.List;

@Path("/api/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryResource {

    private final InventoryApplicationService inventoryAppService;

    @Inject
    public InventoryResource(InventoryApplicationService inventoryAppService) {
        this.inventoryAppService = inventoryAppService;
    }

    @POST
    @Path("/adjust")
    @RolesAllowed("Admin")
    public Response adjustStock(@Valid AdjustStockRequest request) {
        return Response.ok(inventoryAppService.adjustStock(request)).build();
    }

    @GET
    @RolesAllowed({"Admin", "User"})
    public Response getAllStock() {
        return Response.ok(inventoryAppService.getAllStock()).build();
    }

    @GET
    @Path("/{productId}")
    @RolesAllowed({"Admin", "User"})
    public Response getStock(@PathParam("productId") Long productId) {
        return Response.ok(inventoryAppService.getStock(productId)).build();
    }

    @GET
    @Path("/{productId}/movements")
    @RolesAllowed({"Admin", "User"})
    public Response getMovements(
            @PathParam("productId") Long productId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<InventoryMovementResponse> response = inventoryAppService.getMovements(productId, page, size);
        return Response.ok(response).build();
    }
}

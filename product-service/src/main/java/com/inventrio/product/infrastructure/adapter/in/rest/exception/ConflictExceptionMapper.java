package com.inventrio.product.infrastructure.adapter.in.rest.exception;

import com.inventrio.product.domain.exception.ConflictException;
import com.inventrio.product.application.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(ConflictException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}

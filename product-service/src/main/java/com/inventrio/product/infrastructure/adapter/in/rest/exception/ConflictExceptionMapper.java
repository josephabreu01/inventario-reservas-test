package com.inventrio.product.infrastructure.adapter.in.rest.exception;

import com.inventrio.product.domain.exception.ConflictException;
import com.inventrio.product.application.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    private static final Logger LOG = Logger.getLogger(ConflictExceptionMapper.class);

    @Override
    public Response toResponse(ConflictException exception) {
        LOG.warnf("Conflict: %s", exception.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}

package com.inventrio.inventory.infrastructure.adapter.in.rest.exception;

import com.inventrio.inventory.domain.exception.NotFoundException;
import com.inventrio.inventory.application.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger LOG = Logger.getLogger(NotFoundExceptionMapper.class);

    @Override
    public Response toResponse(NotFoundException exception) {
        LOG.debugf("Resource not found: %s", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}

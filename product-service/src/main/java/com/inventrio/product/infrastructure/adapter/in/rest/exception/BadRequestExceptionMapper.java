package com.inventrio.product.infrastructure.adapter.in.rest.exception;

import com.inventrio.product.application.ErrorResponse;
import com.inventrio.product.domain.exception.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    private static final Logger LOG = Logger.getLogger(BadRequestExceptionMapper.class);

    @Override
    public Response toResponse(BadRequestException exception) {
        LOG.warnf("Bad request: %s", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}

package com.inventrio.product.infrastructure.adapter.in.rest.exception;

import com.inventrio.product.application.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.errorf(exception, "Unhandled exception: %s", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("An unexpected error occurred. Please try again later."))
                .build();
    }
}

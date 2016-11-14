package com.stormpath.shiro.jaxrs;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler;
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler;
import com.stormpath.shiro.jaxrs.util.ResponseProxy;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * JAX-RS exception mapper used to map Shiro {@link AuthorizationException}s to Stormpath {@link UnauthenticatedHandler} and
 * {@link UnauthenticatedHandler}.
 * @since 0.8.0
 */
public class StormpathShiroExceptionMapper implements ExceptionMapper<AuthorizationException> {

    private static final String UNAUTHENTICATED_HANDLER = "stormpath.web.authc.unauthenticatedHandler";
    private static final String UNAUTHORIZED_HANDLER = "stormpath.web.authz.unauthorizedHandler";

    private final Logger log = LoggerFactory.getLogger(StormpathShiroExceptionMapper.class);

    private final UnauthorizedHandler unauthorizedHandler;
    private final UnauthenticatedHandler unauthenticatedHandler;

    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;

    public StormpathShiroExceptionMapper(@Context ServletContext servletContext,
                                         @Context HttpServletRequest servletRequest,
                                         @Context HttpServletResponse servletResponse) throws ServletException {

        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;

        Config config =ConfigResolver.INSTANCE.getConfig(servletContext);
        unauthenticatedHandler = config.getInstance(UNAUTHENTICATED_HANDLER);
        unauthorizedHandler = config.getInstance(UNAUTHORIZED_HANDLER);
    }

    @Override
    public Response toResponse(AuthorizationException exception) {

        ResponseProxy responseProxy = new ResponseProxy(servletResponse);

        try {
            if (exception instanceof UnauthorizedException) {
                unauthorizedHandler.onUnauthorized(servletRequest, responseProxy);
            } else {
                unauthenticatedHandler.onAuthenticationRequired(servletRequest, responseProxy);
            }
        } catch (Exception e) {
            log.error("Failed to handle AuthorizationException", e);
            responseProxy.setStatus(500);
        }

        return responseProxy.toResponse();
    }
}
package com.stormpath.shiro.jaxrs.util;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;


/**
 * This @{link {@link HttpServletResponseWrapper}, wraps a
 */
public class ResponseProxy extends HttpServletResponseWrapper {

    private final Response.ResponseBuilder jaxrsResponseBuilder;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public ResponseProxy(HttpServletResponse response) {
        super(response);
        jaxrsResponseBuilder = Response.status(response.getStatus());
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(location);
        try {
            jaxrsResponseBuilder
                .location(new URI(location))
                .status(this.getStatus());

        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void setDateHeader(String name, long date) {
        super.setDateHeader(name, date);
        jaxrsResponseBuilder.header(name, new Date(date));
    }

    @Override
    public void addDateHeader(String name, long date) {
        super.addDateHeader(name, date);
        jaxrsResponseBuilder.header(name, new Date(date));
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        jaxrsResponseBuilder.header(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        jaxrsResponseBuilder.header(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        super.setIntHeader(name, value);
        jaxrsResponseBuilder.header(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        super.addIntHeader(name, value);
        jaxrsResponseBuilder.header(name, value);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        jaxrsResponseBuilder.status(sc);
    }

    public Response toResponse() {
        return jaxrsResponseBuilder.build();
    }
}

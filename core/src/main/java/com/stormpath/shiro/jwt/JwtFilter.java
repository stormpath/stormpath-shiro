package com.stormpath.shiro.jwt;

/**
 * Created by vagrant on 8/16/16.
 */
import org.apache.shiro.subject.Subject;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/*This filter retrieves the JWT token from the authorization header and wraps it in a JwtAuthenticationToken. This should be used with JWTApplicationRealm
to support OAuth+Jwt authentication. In addition to the example shiro.ini in the wiki, simply add:
[main]
jwt = shiro.JwtAuthenticationFilter
jwt.authzScheme = Bearer

and change the realm to:
stormpathRealm = com.stormpath.shiro.realm.JWTApplicationRealm

The authorization header should look like: Authorization: Bearer <jwt token>
 */

public class JwtFilter extends AuthenticatingFilter {

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    private String authzScheme = "Bearer";

    public String getAuthzScheme() {
        return authzScheme;
    }

    public void setAuthzScheme(String authzScheme) {
        this.authzScheme = authzScheme;
    }

    public JwtFilter() {
        System.out.println("FILTER");
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        Subject subject = getSubject(request, response);
        subject.login(token);

        System.out.println(subject.isAuthenticated());

        return subject.isAuthenticated();
    }

    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }

    public String getPrincipalsAndCredentials(String authorizationHeader){
        if (authorizationHeader == null) {
            return null;
        }
        String[] authTokens = authorizationHeader.split(" ");
        if (authTokens == null || authTokens.length != 2) {
            return null;
        }
        return authTokens[1];
    }


    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        String authorizationHeader = getAuthzHeader(request);
        if (authorizationHeader == null || authorizationHeader.length() == 0) {
            return null;
        }

        String token = getPrincipalsAndCredentials(authorizationHeader);//, request);
        if (token == null) {
            return null;
        }

        String message = token;
        String sub = token;

        return createToken(message, sub, request, response);
    }

    @Override
    protected AuthenticationToken createToken(String message, String sub, ServletRequest request, ServletResponse response) {
        return new JwtAuthenticationToken(message, sub);
    }



}

package com.sope.configuration;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class PreAuthTokenFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final String X_AUTH_TOKEN_HEADERNAME = "X-Auth-Token";

    @Inject
    public PreAuthTokenFilter(AuthenticationManager authenticationManager) {
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        return getAutenticationToken(httpServletRequest);
    }
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return getAutenticationToken(httpServletRequest);
    }

    private Object getAutenticationToken(HttpServletRequest httpServletRequest) {
        if (httpServletRequest instanceof HttpServletRequest) {
            // Possible way to get www-page working
            Cookie token = WebUtils.getCookie(httpServletRequest, "key");
            if (token != null) {
                return token.getValue();
            }
        }
        return httpServletRequest.getHeader(X_AUTH_TOKEN_HEADERNAME);
    }

}

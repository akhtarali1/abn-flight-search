package com.abn.flight.search.configuration;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.NonNull;

/**
 * Api Key authentication filter which validates all incoming requests
 *
 * @author Akhtar
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String apiKeyName;
    private final String apiKeyValue;

    /**
     * Constructor API key authentication filter
     *
     * @param environment {@link Environment}
     */
    public ApiKeyAuthenticationFilter(Environment environment) {
        apiKeyName = environment.getProperty("abn.api.key.name");
        apiKeyValue = environment.getProperty("abn.api.key.value");
    }

    /**
     * Overrides default filter and authenticates all incoming requests
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param chain    {@link FilterChain}
     * @throws IOException      IO exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                 @NonNull FilterChain chain)
        throws IOException, ServletException {

        String apiKeyFromRequest = request.getHeader(apiKeyName);
        if (isNotEmpty(apiKeyFromRequest)) {
            if (apiKeyValue.equals(apiKeyFromRequest)) {
                ApiKeyAuthenticationToken apiToken = new ApiKeyAuthenticationToken(apiKeyFromRequest, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(apiToken);
            }
            else {
                response.setStatus(401);
                response.getWriter().write("Invalid API Key");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

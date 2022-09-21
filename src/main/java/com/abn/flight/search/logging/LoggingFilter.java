package com.abn.flight.search.logging;

import static com.abn.flight.search.logging.LoggingFilterConfiguration.DEFAULT_CORRELATION_HEADER;
import static com.abn.flight.search.logging.LoggingFilterConfiguration.DEFAULT_MDC_UUID_KEY;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Akhtar
 */
@EqualsAndHashCode(callSuper = false)
@WebFilter("/abn")
@Slf4j
public class LoggingFilter implements Filter {

    private final String correlationHeader;
    private final String mdcId;

    /**
     * Default Constructor
     */
    public LoggingFilter() {
        correlationHeader = DEFAULT_CORRELATION_HEADER;
        mdcId = DEFAULT_MDC_UUID_KEY;
    }

    /**
     * Constructor for initializing bean
     *
     * @param correlationHeader unique Id header
     * @param mdcId             MDC key
     */
    public LoggingFilter(final String correlationHeader, final String mdcId) {
        this.correlationHeader = correlationHeader;
        this.mdcId = mdcId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getRequestURI().contains("abn")) {
            try {
                final String correlationId = extractCorrelationId(request);
                MDC.put(mdcId, correlationId);
                response = wrapResponse(response);
                response.addHeader(correlationHeader, correlationId);
                log.info("{} Request {} ", request.getMethod(), request.getRequestURI());

                chain.doFilter(request, response);
            }
            finally {
                logResponse(response);
                MDC.clear();
                updateResponse(response);
            }
        }
        else {
            chain.doFilter(request, response);
        }

    }

    private String extractCorrelationId(final HttpServletRequest request) {
        final String correlationId;
        if (StringUtils.isNotEmpty(request.getHeader(correlationHeader))) {
            correlationId = request.getHeader(correlationHeader);
        }
        else {
            correlationId = UUID.randomUUID().toString().toUpperCase();
        }
        return correlationId;
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        }
        else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    private void logResponse(HttpServletResponse response) {
        ContentCachingResponseWrapper contentResponse = wrapResponse(response);
        try {
            String body = new String(contentResponse.getContentAsByteArray(), contentResponse.getCharacterEncoding());
            Stream.of(body.split("\r\n|\r|\n")).forEach(line -> log.info("Response: {}", line));
        }
        catch (UnsupportedEncodingException e) {
            log.error("Unable to log content:", e);
        }
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
            WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }
}
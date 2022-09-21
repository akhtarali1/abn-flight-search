package com.abn.flight.search.logging;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Akhtar
 */
@Configuration
public class LoggingFilterConfiguration {

    public static final String DEFAULT_CORRELATION_HEADER = "Correlation_Id";
    public static final String DEFAULT_MDC_UUID_KEY = "LoggingFilter.UUID";

    /**
     * Servlet Registration Bean
     *
     * @return filter registration bean
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> servletRegistrationBean() {
        final FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        final LoggingFilter log4jMDCFilterFilter = new LoggingFilter(DEFAULT_CORRELATION_HEADER, DEFAULT_MDC_UUID_KEY);
        registrationBean.setFilter(log4jMDCFilterFilter);
        registrationBean.addUrlPatterns("/abn/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
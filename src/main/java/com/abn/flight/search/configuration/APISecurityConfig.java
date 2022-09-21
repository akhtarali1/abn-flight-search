package com.abn.flight.search.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * API security configuration
 *
 * @author Akhtar
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig {

    private final Environment environment;

    /**
     * Constructor API security configuration
     *
     * @param environment {@link Environment}
     */
    public APISecurityConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * filter chain bean is alternative of overriding WebSecurityConfigurerAdapter configure method
     * as WebSecurityConfigurerAdapter is deprecated.
     *
     * @param http {@link HttpSecurity}
     * @return security filter chain
     * @throws Exception all exceptions which  are not handled
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .addFilterBefore(new ApiKeyAuthenticationFilter(environment), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/abn/flight/**").authenticated()
            .antMatchers("/").permitAll();

        return http.build();
    }
}

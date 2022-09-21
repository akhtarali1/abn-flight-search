package com.abn.flight.search;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * Flight search application start class
 */
@SpringBootApplication
public class FlightSearchApplication {

    /**
     * Startup method for application
     *
     * @param args input arguments for application startup
     */
    public static void main(String[] args) {
        SpringApplication.run(FlightSearchApplication.class, args);
    }

    /**
     * Bean to add default header of API key for Swagger-UI
     *
     * @return customizable operation
     */
    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> operation.addParametersItem(
            new Parameter()
                .in("header")
                .required(true)
                .description("API key")
                .name("X-API-KEY"));
    }
}

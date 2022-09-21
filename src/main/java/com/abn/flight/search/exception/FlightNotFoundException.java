package com.abn.flight.search.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Handles Flight not found scenarios
 *
 * @author Akhtar
 */
@Getter
@AllArgsConstructor
public class FlightNotFoundException extends RuntimeException {

    private final String flightNumber;
    private final String origin;
    private final String destination;

}

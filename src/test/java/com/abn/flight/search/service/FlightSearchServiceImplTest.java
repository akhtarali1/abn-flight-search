package com.abn.flight.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abn.flight.search.domain.FlightNotFoundException;
import com.abn.flight.search.mapper.FlightMapper;
import com.abn.flight.search.model.Flight;
import com.abn.flight.search.model.SearchContext;
import com.abn.flight.search.persistance.entity.FlightEntity;
import com.abn.flight.search.persistance.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceImplTest {

    private FlightSearchServiceImpl flightSearchService;

    @Mock private FlightRepository flightRepository;
    @Mock private FlightMapper mapper;
    @Mock private FlightEntity flightEntity;

    @BeforeEach
    void setUp() {
        flightSearchService = new FlightSearchServiceImpl(flightRepository, mapper);
    }

    @Test
    @DisplayName("Get all flights matching Origin Destination")
    void getAllFlights() {
        when(flightRepository.findAllByOriginAndDestination(eq("AMS"), eq("DEL"), any())).thenReturn(List.of(flightEntity));
        when(mapper.mapFlightFromEntity(eq(flightEntity), any())).thenReturn(formFlight());
        List<Flight> flights = flightSearchService.getAllFlights(getSearchContext());
        assertFalse(flights.isEmpty());
        Flight flight = flights.get(0);
        assertEquals("AMS", flight.getOrigin());
        assertEquals("DEL", flight.getDestination());
        assertEquals("A101", flight.getFlightNumber());
        assertEquals("850EUR", flight.getPrice());
        assertEquals("5 Hrs", flight.getDuration());
    }

    @Test
    @DisplayName("Throw exception when flight not found")
    void getAllFlightsWhenFlightNotFound() {
        when(flightRepository.findAllByOriginAndDestination(eq("AMS"), eq("DEL"), any())).thenReturn(Collections.emptyList());
        FlightNotFoundException exception = assertThrows(FlightNotFoundException.class,
            () -> flightSearchService.getAllFlights(getSearchContext()));
        assertEquals("AMS", exception.getOrigin());
        assertEquals("DEL", exception.getDestination());
        assertNull(exception.getFlightNumber());
    }

    @Test
    @DisplayName("Get individual Flight details")
    void getFLightDetails() {
        when(flightRepository.findByFlightNumber("A101")).thenReturn(Optional.of(flightEntity));
        when(mapper.mapFlightFromEntity(eq(flightEntity), any())).thenReturn(formFlight());
        Flight flight = flightSearchService.getFLightDetails("A101", LocalDate.now().plusDays(5));
        assertEquals("AMS", flight.getOrigin());
        assertEquals("DEL", flight.getDestination());
        assertEquals("A101", flight.getFlightNumber());
        assertEquals("850EUR", flight.getPrice());
        assertEquals("5 Hrs", flight.getDuration());
    }

    @Test
    @DisplayName("Throw exception when flight not found")
    void getFLightDetailsWhenFlightNotFound() {
        when(flightRepository.findByFlightNumber("A101")).thenReturn(Optional.empty());
        FlightNotFoundException exception = assertThrows(FlightNotFoundException.class,
            () -> flightSearchService.getFLightDetails("A101", LocalDate.now().plusDays(5)));
        assertNull(exception.getOrigin());
        assertNull(exception.getDestination());
        assertEquals("A101", exception.getFlightNumber());
    }

    private SearchContext getSearchContext() {
        return SearchContext.builder()
            .origin("AMS")
            .destination("DEL")
            .departureDate(LocalDate.now().plusDays(10))
            .build();
    }

    private Flight formFlight() {
        return Flight.builder()
            .flightNumber("A101")
            .origin("AMS")
            .destination("DEL")
            .departureTime(LocalTime.now().plusHours(5).toString())
            .arrivalTime(LocalTime.now().plusHours(10).toString())
            .price("850EUR")
            .duration("5 Hrs")
            .build();
    }
}
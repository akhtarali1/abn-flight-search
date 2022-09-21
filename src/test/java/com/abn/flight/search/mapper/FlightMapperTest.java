package com.abn.flight.search.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abn.flight.search.model.Flight;
import com.abn.flight.search.persistance.entity.AirportEntity;
import com.abn.flight.search.persistance.entity.FlightEntity;

@ExtendWith(MockitoExtension.class)
class FlightMapperTest {

    private FlightMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FlightMapper();
    }

    @Test
    void mapFlightFromEntity() {
        Flight flight = mapper.mapFlightFromEntity(flightEntity(LocalTime.of(12, 45)), LocalDate.now());
        assertEquals("2:15 Hrs", flight.getDuration());
        assertEquals("Schiphol, Amsterdam (AMS)", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("12:30", flight.getDepartureTime());
        assertEquals("18:15", flight.getArrivalTime());
    }

    @Test
    void mapFlightFromEntityWhenFlightArrivesNextDay() {
        Flight flight = mapper.mapFlightFromEntity(flightEntity(LocalTime.of(1, 45)), LocalDate.now());
        assertEquals("15:15 Hrs", flight.getDuration());
        assertEquals("Schiphol, Amsterdam (AMS)", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("12:30", flight.getDepartureTime());
        assertEquals("07:15(Next Day)", flight.getArrivalTime());
    }

    @Test
    void mapFlightFromEntityWhenAirportNotFound() {
        FlightEntity flightEntity = flightEntity(LocalTime.of(12, 45));
        flightEntity.setOrigin(null);
        Flight flight = mapper.mapFlightFromEntity(flightEntity, LocalDate.now());
        assertEquals("2:15 Hrs", flight.getDuration());
        assertEquals("", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("10:30", flight.getDepartureTime());
        assertEquals("18:15", flight.getArrivalTime());
    }

    private AirportEntity airportEntity(String airportCode, String airportName, String city, TimeZone timeZone) {
        AirportEntity airportEntity = new AirportEntity();
        airportEntity.setAirportCode(airportCode);
        airportEntity.setAirportName(airportName);
        airportEntity.setAirportTimeZone(timeZone);
        airportEntity.setCity(city);
        return airportEntity;
    }

    private FlightEntity flightEntity(LocalTime arrival) {
        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setFlightNumber("A101");
        flightEntity.setOrigin(airportEntity("AMS", "Schiphol", "Amsterdam", TimeZone.getTimeZone("Europe/Amsterdam")));
        flightEntity.setDestination(airportEntity("DEL", "Indra Airport", "New Delhi", TimeZone.getTimeZone("Asia/Kolkata")));
        flightEntity.setPrice(850.00);
        flightEntity.setCurrency("EUR");
        flightEntity.setDepartureTime(LocalTime.of(10, 30));
        flightEntity.setArrivalTime(arrival);
        return flightEntity;
    }
}
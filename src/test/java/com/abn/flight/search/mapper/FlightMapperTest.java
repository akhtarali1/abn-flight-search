package com.abn.flight.search.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abn.flight.search.model.Flight;
import com.abn.flight.search.persistance.entity.AirportEntity;
import com.abn.flight.search.persistance.entity.FlightEntity;
import com.abn.flight.search.persistance.repository.AirportRepository;

@ExtendWith(MockitoExtension.class)
class FlightMapperTest {

    private FlightMapper mapper;

    @Mock private AirportRepository airportRepository;

    @BeforeEach
    void setUp() {
        mapper = new FlightMapper(airportRepository);
    }

    @Test
    void mapFlightFromEntity() {
        when(airportRepository.findByAirportCode("AMS"))
            .thenReturn(airportEntity("AMS", "Schiphol", "Amsterdam", TimeZone.getTimeZone("Europe/Amsterdam")));
        when(airportRepository.findByAirportCode("DEL"))
            .thenReturn(airportEntity("DEL", "Indra Airport", "New Delhi", TimeZone.getTimeZone("Asia/Kolkata")));
        Flight flight = mapper.mapFlightFromEntity(flightEntity(LocalTime.of(12, 45)), null);
        assertEquals("2:15 Hrs", flight.getDuration());
        assertEquals("Schiphol, Amsterdam (AMS)", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("12:30", flight.getDepartureTime());
        assertEquals("18:15", flight.getArrivalTime());
    }

    @Test
    void mapFlightFromEntityWhenFlightArrivesNextDay() {
        when(airportRepository.findByAirportCode("AMS"))
            .thenReturn(airportEntity("AMS", "Schiphol", "Amsterdam", TimeZone.getTimeZone("Europe/Amsterdam")));
        when(airportRepository.findByAirportCode("DEL"))
            .thenReturn(airportEntity("DEL", "Indra Airport", "New Delhi", TimeZone.getTimeZone("Asia/Kolkata")));
        Flight flight = mapper.mapFlightFromEntity(flightEntity(LocalTime.of(1, 45)), null);
        assertEquals("15:15 Hrs", flight.getDuration());
        assertEquals("Schiphol, Amsterdam (AMS)", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("12:30", flight.getDepartureTime());
        assertEquals("07:15(Next Day)", flight.getArrivalTime());
    }

    @Test
    void mapFlightFromEntityWhenAirportNotFound() {
        when(airportRepository.findByAirportCode("AMS"))
            .thenReturn(Optional.empty());
        when(airportRepository.findByAirportCode("DEL"))
            .thenReturn(airportEntity("DEL", "Indra Airport", "New Delhi", TimeZone.getTimeZone("Asia/Kolkata")));
        Flight flight = mapper.mapFlightFromEntity(flightEntity(LocalTime.of(12, 45)), null);
        assertEquals("2:15 Hrs", flight.getDuration());
        assertEquals("AMS", flight.getOrigin());
        assertEquals("Indra Airport, New Delhi (DEL)", flight.getDestination());
        assertEquals("10:30", flight.getDepartureTime());
        assertEquals("18:15", flight.getArrivalTime());
    }

    private Optional<AirportEntity> airportEntity(String airportCode, String airportName, String city, TimeZone timeZone) {
        AirportEntity airportEntity = new AirportEntity();
        airportEntity.setAirportCode(airportCode);
        airportEntity.setAirportName(airportName);
        airportEntity.setAirportTimeZone(timeZone);
        airportEntity.setCity(city);
        return Optional.of(airportEntity);
    }

    private FlightEntity flightEntity(LocalTime arrival) {
        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setFlightNumber("A101");
        flightEntity.setOrigin("AMS");
        flightEntity.setDestination("DEL");
        flightEntity.setPrice("850EUR");
        flightEntity.setDepartureTime(LocalTime.of(10, 30));
        flightEntity.setArrivalTime(arrival);
        return flightEntity;
    }
}
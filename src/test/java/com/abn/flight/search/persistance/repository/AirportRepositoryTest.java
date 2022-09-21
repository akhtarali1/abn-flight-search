package com.abn.flight.search.persistance.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.abn.flight.search.persistance.entity.AirportEntity;

@DataJpaTest
class AirportRepositoryTest {

    @Autowired private AirportRepository airportRepository;

    @Test
    void findByAirportCode() {
        Optional<AirportEntity> airportEntity = airportRepository.findByAirportCode("AMS");
        assertTrue(airportEntity.isPresent());
    }

    @Test
    void findByAirportCodeWhenAirportNotFound() {
        Optional<AirportEntity> airportEntity = airportRepository.findByAirportCode("JFK");
        assertFalse(airportEntity.isPresent());
    }
}
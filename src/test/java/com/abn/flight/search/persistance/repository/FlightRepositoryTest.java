package com.abn.flight.search.persistance.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import com.abn.flight.search.model.SortFields;
import com.abn.flight.search.persistance.entity.FlightEntity;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired private FlightRepository flightRepository;

    @Test
    void findAllByOriginAndDestination() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, SortFields.departureTime.name());
        List<FlightEntity> flightEntities = flightRepository.findAllByOriginAndDestination("AMS", "DEL", Sort.by(order));
        assertFalse(flightEntities.isEmpty());
    }

    @Test
    void findAllByOriginAndDestinationWithNoMatch() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, SortFields.price.name());
        List<FlightEntity> flightEntities = flightRepository.findAllByOriginAndDestination("AMS", "JFK", Sort.by(order));
        assertTrue(flightEntities.isEmpty());
    }

    @Test
    void findByFlightNumber() {
        Optional<FlightEntity> flightEntity = flightRepository.findByFlightNumber("A101");
        assertTrue(flightEntity.isPresent());
    }

    @Test
    void findByFlightNumberWhenNotFound() {
        Optional<FlightEntity> flightEntity = flightRepository.findByFlightNumber("Z101");
        assertFalse(flightEntity.isPresent());
    }
}
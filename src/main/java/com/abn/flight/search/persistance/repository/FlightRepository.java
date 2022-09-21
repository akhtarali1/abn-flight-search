package com.abn.flight.search.persistance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.abn.flight.search.persistance.entity.FlightEntity;

/**
 * Flight repository
 *
 * @author Akhtar
 */
@Repository
public interface FlightRepository extends PagingAndSortingRepository<FlightEntity, Long> {

    /**
     * Get all available flight matching origin & destination
     *
     * @param origin      flight origin
     * @param destination flight destination
     * @param sortOrder sort order
     * @return available flights matching origin & destination combination
     */
    List<FlightEntity> findAllByOriginAndDestination(String origin, String destination, Sort sortOrder);

    /**
     * Get flight details by flight number
     *
     * @param flightNumber flight number
     * @return flight details
     */
    Optional<FlightEntity> findByFlightNumber(String flightNumber);
}

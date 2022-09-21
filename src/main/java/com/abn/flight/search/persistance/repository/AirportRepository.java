package com.abn.flight.search.persistance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abn.flight.search.persistance.entity.AirportEntity;

/**
 * Airport repository with required query methods
 *
 * @author Akhtar
 */
@Repository
public interface AirportRepository extends JpaRepository<AirportEntity, Long> {

    /**
     * Get airport details by airport code
     *
     * @param airportCode airport code
     * @return airport entity
     */
    Optional<AirportEntity> findByAirportCode(String airportCode);
}

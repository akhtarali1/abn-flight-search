package com.abn.flight.search.persistance.entity;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Flight entity
 *
 * @author AKhtar
 */
@Entity
@Data
@Table(name = "FLIGHT")
public class FlightEntity {

    @Id
    private Long id;
    @NotNull
    private String flightNumber;

    @NotNull
    @JoinColumn(name = "ORIGIN", nullable = false)
    @ManyToOne
    private AirportEntity origin;

    @NotNull
    @JoinColumn(name = "DESTINATION", nullable = false)
    @ManyToOne
    private AirportEntity destination;

    @NotNull
    private LocalTime departureTime;
    @NotNull
    private LocalTime arrivalTime;
    @NotNull
    private Double price;
    @NotNull
    private String currency;

}

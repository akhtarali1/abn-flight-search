package com.abn.flight.search.persistance.entity;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "FLIGHTS")
public class FlightEntity {

    @Id
    private Long id;
    @NotNull
    private String flightNumber;
    @NotNull
    private String origin;
    @NotNull
    private String destination;
    @NotNull
    private LocalTime departureTime;
    @NotNull
    private LocalTime arrivalTime;
    @NotNull
    private String price;

}

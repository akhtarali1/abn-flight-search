package com.abn.flight.search.persistance.entity;

import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Airport entity
 *
 * @author Akhtar
 */
@Entity
@Data
@Table(name = "AIRPORTS")
public class AirportEntity {

    @Id
    private Long id;
    @NotNull
    private String airportCode;
    private String airportName;
    private String city;
    private TimeZone airportTimeZone;

}

package com.abn.flight.search.model;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Flight rest model
 *
 * @author Akhtar
 */
@Getter
@Builder
@EqualsAndHashCode(callSuper = false, exclude = {"flightNumber", "origin", "destination", "departureDate"})
@Schema(description = "Flight details based on departure date")
public class Flight extends RepresentationModel<Flight> {

    @Schema(description = "Flight Number")
    @NotNull
    private String flightNumber;

    @Schema(description = "Flight Origin")
    @NotNull
    private String origin;

    @Schema(description = "Flight Destination")
    @NotNull
    private String destination;

    @Schema(description = "Flight departure date")
    @NotNull
    private String departureDate;

    @Schema(description = "Flight departure local time")
    @NotNull
    private String departureTime;

    @Schema(description = "FLight arrival local time with next day indicator if any")
    @NotNull
    private String arrivalTime;

    @Schema(description = "Flight price")
    @NotNull
    private String price;

    @Schema(description = "Flight duration in hours & minutes format")
    @NotNull
    private String duration;

}

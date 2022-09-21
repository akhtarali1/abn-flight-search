package com.abn.flight.search.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.abn.flight.search.model.Flight;
import com.abn.flight.search.model.SearchContext;
import com.abn.flight.search.service.FlightSearchServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Flight Search Controller
 */
@RestController
@RequestMapping(value = "/abn/flight/search", produces = APPLICATION_JSON_VALUE)
public class FlightSearchController {

    private final FlightSearchServiceImpl flightSearchService;

    /**
     * Constructor Flight Search controller
     *
     * @param flightSearchService {@link FlightSearchServiceImpl}
     */
    public FlightSearchController(FlightSearchServiceImpl flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    /**
     * Get all available flights based on query parameter
     *
     * @param searchContext flight search query parameters
     * @return list of available flights
     */
    @Operation(summary = "Get Flights based on Origin and Destination")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
        description = "Flights retrieved", content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Flight.class))})})
    @GetMapping
    @ResponseStatus(OK)
    public List<Flight> getFlights(@ModelAttribute @Valid SearchContext searchContext) {

        List<Flight> flights = flightSearchService.getAllFlights(searchContext);
        flights.forEach(flight -> flight.add(linkTo(methodOn(FlightSearchController.class).getFLightDetails(flight.getFlightNumber(), searchContext.getDepartureDate())).withSelfRel()));
        return flights;
    }

    /**
     * Get individual enriched flight details
     *
     * @param flightNumber  flight number
     * @param departureDate departure date to know exact flight
     * @return enriched flight details
     */
    @Operation(summary = "Get flight details by flight number")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "FLight details retrieved", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))})})
    @GetMapping(value = "/{flightNumber}")
    @ResponseStatus(OK)
    public Flight getFLightDetails(@PathVariable String flightNumber, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        return flightSearchService.getFLightDetails(flightNumber, departureDate);
    }
}

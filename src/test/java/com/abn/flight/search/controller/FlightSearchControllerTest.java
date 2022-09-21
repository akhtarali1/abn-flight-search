package com.abn.flight.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abn.flight.search.domain.FlightNotFoundException;
import com.abn.flight.search.model.Flight;
import com.abn.flight.search.service.FlightSearchServiceImpl;

@WebMvcTest(value = FlightSearchController.class)
class FlightSearchControllerTest {

    private static final String URL = "/abn/flight/search";

    @Autowired private MockMvc mockMvc;
    @MockBean private FlightSearchServiceImpl flightSearchService;

    @Test
    void getFlights() throws Exception {
        given(flightSearchService.getAllFlights(any())).willReturn(List.of(formFlight()));
        mockMvc.perform(get(URL)
                .param("origin", "AMS")
                .param("destination", "DEL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].origin").value("AMS"))
            .andExpect(jsonPath("$.[0].destination").value("DEL"))
            .andExpect(jsonPath("$.[0].price").value("850EUR"))
            .andExpect(jsonPath("$.[0].duration").value("5 Hrs"));
    }

    @Test
    void getFlightsWhenFlightNotFound() throws Exception {
        given(flightSearchService.getAllFlights(any())).willThrow(new FlightNotFoundException(null, "AMS", "DEL"));
        mockMvc.perform(get(URL)
                .param("origin", "AMS")
                .param("destination", "DEL"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("No flights found from AMS to DEL"));
    }

    @Test
    void getFlightsWhenOriginNotPassed() throws Exception {
        mockMvc.perform(get(URL)
                .param("destination", "DEL"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getFLightDetails() throws Exception {
        given(flightSearchService.getFLightDetails("A101", LocalDate.of(2022, 12, 10)))
            .willReturn(formFlight());
        mockMvc.perform(get(URL + "/A101")
                .param("departureDate", "2022-12-10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.origin").value("AMS"))
            .andExpect(jsonPath("$.destination").value("DEL"))
            .andExpect(jsonPath("$.price").value("850EUR"))
            .andExpect(jsonPath("$.duration").value("5 Hrs"));
    }

    @Test
    void getFLightDetailsWhenFlightNotFound() throws Exception {
        given(flightSearchService.getFLightDetails("A101", LocalDate.of(2022, 12, 10)))
            .willThrow(new FlightNotFoundException("A101", null, null));
        mockMvc.perform(get(URL + "/A101")
                .param("departureDate", "2022-12-10"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("Flight: A101 not found"));
    }

    private Flight formFlight() {
        return Flight.builder()
            .flightNumber("A101")
            .origin("AMS")
            .destination("DEL")
            .departureTime(LocalTime.now().plusHours(5).toString())
            .arrivalTime(LocalTime.now().plusHours(10).toString())
            .price("850EUR")
            .duration("5 Hrs")
            .build();
    }
}
package com.abn.flight.search.mapper;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

import com.abn.flight.search.model.Flight;
import com.abn.flight.search.persistance.entity.AirportEntity;
import com.abn.flight.search.persistance.entity.FlightEntity;

/**
 * Flight model Mapper
 */
@Component
public class FlightMapper {

    /**
     * Map flight model from entity
     *
     * @param flightEntity  flight entity from DB
     * @param departureDate departure date in UTC format
     * @return flight model
     */
    public Flight mapFlightFromEntity(FlightEntity flightEntity, LocalDate departureDate) {
        AtomicBoolean isNextDayArrival = new AtomicBoolean(false);
        String duration = getFlightDuration(flightEntity, isNextDayArrival);

        Optional<AirportEntity> origin = Optional.ofNullable(flightEntity.getOrigin());
        Optional<AirportEntity> destination = Optional.ofNullable(flightEntity.getDestination());

        return Flight.builder()
            .flightNumber(flightEntity.getFlightNumber())
            .origin(getAirportName(origin))
            .destination(getAirportName(destination))
            .departureDate(departureDate.toString())
            .departureTime(getAirportLocalTime(flightEntity.getDepartureTime(), departureDate, origin, false))
            .arrivalTime(getAirportLocalTime(flightEntity.getArrivalTime(), departureDate, destination, isNextDayArrival.get()))
            .price(flightEntity.getPrice().toString() + SPACE + flightEntity.getCurrency())
            .duration(duration)
            .build();
    }

    private String getFlightDuration(FlightEntity flightEntity, AtomicBoolean isNextDayArrival) {
        Duration calculatedDuration = Duration.between(flightEntity.getDepartureTime(), flightEntity.getArrivalTime());
        if (calculatedDuration.isNegative() || calculatedDuration.isZero()) {
            isNextDayArrival.set(true);
            calculatedDuration = calculatedDuration.plusDays(1);
        }
        return String.format("%d:%02d Hrs", calculatedDuration.toHours(), calculatedDuration.toMinutesPart());
    }

    private String getAirportLocalTime(LocalTime utcTime, LocalDate departureDate,
                                       Optional<AirportEntity> airportDetails, boolean isArrivesNextDay) {
        Instant utcDateTime = departureDate.atTime(utcTime).toInstant(ZoneOffset.UTC);
        String localTime = airportDetails.map(airport -> utcDateTime
                .atZone(airport.getAirportTimeZone().toZoneId())
                .toLocalTime())
            .orElse(utcTime)
            .format(DateTimeFormatter.ofPattern("HH:mm"));

        if (isArrivesNextDay) {
            // Update arrival time with next day indicator
            localTime = localTime + "(Next Day)";
        }

        return localTime;
    }

    private String getAirportName(Optional<AirportEntity> origin) {
        return origin
            .map(o -> String.format("%s, %s (%s)", o.getAirportName(), o.getCity(), o.getAirportCode()))
            .orElse(EMPTY);
    }

}

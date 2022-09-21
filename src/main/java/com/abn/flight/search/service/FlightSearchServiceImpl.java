package com.abn.flight.search.service;

import static com.abn.flight.search.domain.SortFields.departureTime;
import static com.abn.flight.search.domain.SortFields.duration;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.abn.flight.search.domain.FlightNotFoundException;
import com.abn.flight.search.domain.SortFields;
import com.abn.flight.search.mapper.FlightMapper;
import com.abn.flight.search.model.Flight;
import com.abn.flight.search.model.SearchContext;
import com.abn.flight.search.persistance.entity.FlightEntity;
import com.abn.flight.search.persistance.repository.FlightRepository;

/**
 * Flight service business implementation
 *
 * @author Akhtar
 */
@Service
public class FlightSearchServiceImpl {

    private final FlightRepository flightRepository;
    private final FlightMapper mapper;

    /**
     * Flight service implementation constructor
     *
     * @param flightRepository {@link FlightRepository}
     * @param mapper           {@link FlightMapper}
     */
    public FlightSearchServiceImpl(FlightRepository flightRepository, FlightMapper mapper) {
        this.flightRepository = flightRepository;
        this.mapper = mapper;
    }

    /**
     * Get all flights matching search context
     *
     * @param searchContext search queries details
     * @return available flights matching search context
     */
    public List<Flight> getAllFlights(SearchContext searchContext) {
        String origin = searchContext.getOrigin();
        String destination = searchContext.getDestination();
        LocalDate departureDate = searchContext.getDepartureDate();

        String sortByField = searchContext.getSortBy().equals(duration) ? departureTime.name() : searchContext.getSortBy().name();
        Sort.Order order = new Sort.Order(searchContext.getOrderBy(), sortByField);
        List<FlightEntity> flightEntities = flightRepository.findAllByOriginAndDestination(origin, destination,Sort.by(order));
        if (flightEntities.isEmpty()) {
            throw new FlightNotFoundException(null, origin, destination);
        }

        List<Flight> flights =  flightEntities
            .stream()
            .map(flight -> mapper.mapFlightFromEntity(flight, departureDate))
            .collect(toList());

        sortBasedOnDuration(searchContext, flights);
        return flights;
    }

    /**
     * Sort flights based on duration if requested
     * TODO This is not efficient way of sorting duration.
     * TODO I would like to have duration column in DB which automatically calculates duration on each inset or update via trigger
     * TODO due to time constraints going with this approach which is not at all recommended.
     *
     * @param searchContext search query parameters
     * @param flights       available flights
     */
    private void sortBasedOnDuration(SearchContext searchContext, List<Flight> flights) {
        if (SortFields.duration.equals(searchContext.getSortBy())) {
            flights.sort((f1, f2) -> {
                if (Sort.Direction.ASC.equals(searchContext.getOrderBy())) {
                    return f1.getDuration().compareToIgnoreCase(f2.getDuration());
                }
                else {
                    return f2.getDuration().compareToIgnoreCase(f1.getDuration());
                }
            });
        }
    }

    /**
     * Get flight details based on flight number
     *
     * @param flightNumber  flight number
     * @param departureDate departure date of flight
     * @return flight details
     */
    public Flight getFLightDetails(String flightNumber, LocalDate departureDate) {
        return flightRepository.findByFlightNumber(flightNumber)
            .map(flight -> mapper.mapFlightFromEntity(flight, departureDate))
            .orElseThrow(() -> new FlightNotFoundException(flightNumber, null, null));
    }


}

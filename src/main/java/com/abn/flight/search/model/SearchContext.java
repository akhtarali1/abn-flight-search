package com.abn.flight.search.model;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Search context for combining all input parameters
 *
 * @author Akhtar
 */
@Getter
@Builder
public class SearchContext {

    @Schema(description = "Origin airport code")
    @NotNull
    @Size(min = 1, max = 3)
    private String origin;

    @Schema(description = "destination airport code")
    @NotNull
    @Size(min = 1, max = 3)
    private String destination;

    @Schema(description = "Flight departure date(yyyy-mm-dd) format if known, else current date plus 10days is considered by default")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate departureDate;

    @Schema(description = "Optional Sort by fields. Default price sorting is considered")
    private SortFields sortBy;
    @Schema(description = "Optional sorting order Ascending or Descending. Default value is Ascending")
    private Direction orderBy;

    /**
     * Get departure date
     *
     * @return departure date
     */
    public LocalDate getDepartureDate() {
        // Setting default date to now() + 10 if not provided
        return ofNullable(departureDate)
            .orElseGet(() -> LocalDate.now().plusDays(10));
    }

    /**
     * Get soring order ASC/DSC
     *
     * @return soring order by
     */
    public Direction getOrderBy() {
        return ofNullable(orderBy).orElse(Direction.ASC);
    }

    /**
     * Get sorting field
     *
     * @return sorting field
     */
    public SortFields getSortBy() {
        return ofNullable(sortBy).orElse(SortFields.price);
    }
}

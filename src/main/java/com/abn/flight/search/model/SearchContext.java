package com.abn.flight.search.model;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;

import com.abn.flight.search.domain.SortFields;

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

    private SortFields sortBy;
    private Direction orderBy;

    public Direction getOrderBy() {
        orderBy = ofNullable(orderBy).orElse(Direction.ASC);
        return orderBy;
    }

    public SortFields getSortBy() {
        sortBy = ofNullable(sortBy).orElse(SortFields.price);
        return sortBy;
    }
}

package com.abn.flight.search.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Model to handle error/unhandled scenarios
 */
@Schema(description = "Model to handle error/unhandled scenarios")
@Getter
@Builder
public class Error {

    @Schema(description = "error code")
    private String code;

    @Schema(description = "error message")
    private String message;
}

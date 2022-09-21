package com.abn.flight.search.controller;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.abn.flight.search.exception.FlightNotFoundException;
import com.abn.flight.search.model.Error;

import lombok.NonNull;

/**
 * Exception HandlerController
 *
 * @author Akhtar
 */
@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    private static final String ERROR_CODE = "1000";
    private static final String TECHNICAL_ERROR_CODE = "2000";

    /**
     * Handles FlightNotFoundException Exceptions and converts into error.
     *
     * @param exception catches FlightNotFoundException with ID
     * @return error with code and description
     */
    @ExceptionHandler({FlightNotFoundException.class})
    public ResponseEntity<Object> handleFlightNotFoundExceptions(FlightNotFoundException exception) {
        log.error("Id not found", exception);
        String message;
        if (isNotEmpty(exception.getFlightNumber())) {
            message = String.format("Flight: %s not found", exception.getFlightNumber());
        }
        else {
            message = String.format("No flights found from %s to %s", exception.getOrigin(), exception.getDestination());
        }
        return formErrorResponse(message, ERROR_CODE, BAD_REQUEST);
    }

    /**
     * Handles all Method ArgumentType Mismatch Exception and converts in error.
     *
     * @param exception catches all Method ArgumentType Mismatch Exception and converts to error
     * @return error with code and description
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.error("Method ArgumentType Mismatch Exception", exception);
        String message = exception.getName() + " should be of type " + exception.getRequiredType().getName();
        return formErrorResponse(message, ERROR_CODE, BAD_REQUEST);
    }

    /**
     * Handles all unhandled exceptions and converts in error.
     *
     * @param exception catches all unhandled exceptions and converts to error
     * @return error with code and description
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleExceptions(Exception exception) {
        log.error("Unhandled technical exception", exception);
        return formErrorResponse("Unexpected technical error occurred", TECHNICAL_ERROR_CODE, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final @NonNull HttpHeaders headers, final @NonNull HttpStatus status,
                                                                 final @NonNull WebRequest request) {
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();
        return formErrorResponse(error, ERROR_CODE, BAD_REQUEST);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, //
                                                                                   @NonNull HttpStatus status, @NonNull WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return formErrorResponse(error, ERROR_CODE, BAD_REQUEST);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status,
                                                                            @NonNull WebRequest request) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return formErrorResponse(error, ERROR_CODE, NOT_FOUND);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers, //
                                                                                  @NonNull HttpStatus status, @NonNull WebRequest request) {
        String builder = ex.getMethod() + " method is not supported for this request.";
        return formErrorResponse(builder, ERROR_CODE, METHOD_NOT_ALLOWED);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, @NonNull HttpHeaders headers, //
                                                                              @NonNull HttpStatus status, @NonNull WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return formErrorResponse(builder.substring(0, builder.length() - 2), ERROR_CODE, UNSUPPORTED_MEDIA_TYPE);
    }

    private ResponseEntity<Object> formErrorResponse(String message, String code, HttpStatus statusCode) {
        Error error = Error.builder().code(code).message(message).build();
        return new ResponseEntity<>(error, statusCode);
    }

}

package io.github.sigridpihel.deliveryfee.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleUsageForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleVehicleUsageForbidden(VehicleUsageForbiddenException ex) {
        log.warn("Vehicle usage forbidden: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(WeatherDataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWeatherDataNotFound(WeatherDataNotFoundException ex) {
        log.warn("Weather data not found: {}", ex.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(IncompleteWeatherDataException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteWeatherData(IncompleteWeatherDataException ex) {
        log.warn("Incomplete weather data: {}", ex.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(RegionalBaseFeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRegionalBaseFeeNotFound(RegionalBaseFeeNotFoundException ex) {
        log.error("Regional base fee not found: {}", ex.getMessage());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid value '{}' for request parameter '{}'", ex.getValue(), ex.getName());
        return error(HttpStatus.BAD_REQUEST, "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getParameterName());
        return error(HttpStatus.BAD_REQUEST, "Required query parameter '" + e.getParameterName() + "' is missing");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        if (e instanceof org.springframework.web.ErrorResponse springError) {
            log.warn("Request failed with {}: {}", springError.getStatusCode(), e.getMessage());
            return error(springError.getStatusCode(), springError.getBody().getDetail());
        }
        log.error("Unexpected error", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatusCode status, String message) {
        ErrorResponse body = new ErrorResponse(message, Instant.now().toString(), null);
        return ResponseEntity.status(status).body(body);
    }
}

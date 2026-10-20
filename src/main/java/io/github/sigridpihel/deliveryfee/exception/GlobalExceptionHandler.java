package io.github.sigridpihel.deliveryfee.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    private static ResponseEntity<ErrorResponse> error(HttpStatusCode status, String message) {
        ErrorResponse body = new ErrorResponse(message, Instant.now().toString(), null);
        return ResponseEntity.status(status).body(body);
    }
}

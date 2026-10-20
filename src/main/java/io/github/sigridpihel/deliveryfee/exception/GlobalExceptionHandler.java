package io.github.sigridpihel.deliveryfee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles weather conditions that forbid the selected vehicle type.
     *
     * @param ex the exception
     * @return 422 Unprocessable Content with the error message
     */
    @ExceptionHandler(VehicleUsageForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleVehicleUsageForbidden(VehicleUsageForbiddenException ex) {
        return error(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    /**
     * Handles requests for a city with no imported weather data.
     *
     * @param ex the exception
     * @return 503 Service Unavailable with the error message
     */
    @ExceptionHandler(WeatherDataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWeatherDataNotFound(WeatherDataNotFoundException ex) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    /**
     * Handles a weather observation missing a value needed for the calculation.
     *
     * @param ex the exception
     * @return 503 Service Unavailable with the error message
     */
    @ExceptionHandler(IncompleteWeatherDataException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteWeatherData(IncompleteWeatherDataException ex) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    /**
     * Handles a missing regional base fee configuration.
     *
     * @param ex the exception
     * @return 500 Internal Server Error with the error message
     */
    @ExceptionHandler(RegionalBaseFeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRegionalBaseFeeNotFound(RegionalBaseFeeNotFoundException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * Handles request parameters that cannot be converted to the expected type,
     * such as an unknown city or vehicle type.
     *
     * @param ex the exception
     * @return 400 Bad Request naming the invalid parameter and value
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST, "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");
    }

    /**
     * Handles a missing required request parameter.
     *
     * @param e the exception
     * @return 400 Bad Request naming the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        return error(HttpStatus.BAD_REQUEST, "Required query parameter '" + e.getParameterName() + "' is missing");
    }

    /**
     * Handles all other exceptions. Spring web errors keep their own status code;
     * anything else becomes 500 with a generic message.
     *
     * @param e the exception
     * @return the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        if (e instanceof org.springframework.web.ErrorResponse springError) {
            return error(springError.getStatusCode(), springError.getBody().getDetail());
        }
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatusCode status, String message) {
        ErrorResponse body = new ErrorResponse(message, Instant.now().toString(), null);
        return ResponseEntity.status(status).body(body);
    }
}

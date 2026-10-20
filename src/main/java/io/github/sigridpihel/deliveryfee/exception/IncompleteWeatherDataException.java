package io.github.sigridpihel.deliveryfee.exception;

public class IncompleteWeatherDataException extends RuntimeException{
    public IncompleteWeatherDataException(String stationName, String missingValue) {
        super(String.format("%s missing for %s", missingValue, stationName));
    }
}

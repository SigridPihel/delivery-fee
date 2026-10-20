package io.github.sigridpihel.deliveryfee.exception;

public class IncompleteWeatherDataException extends RuntimeException{
    /**
     * Creates the exception for a weather value missing from a station's observation.
     *
     * @param stationName  station whose observation is incomplete
     * @param missingValue name of the missing value, e.g. "Wind speed"
     */
    public IncompleteWeatherDataException(String stationName, String missingValue) {
        super(String.format("%s missing for %s", missingValue, stationName));
    }
}

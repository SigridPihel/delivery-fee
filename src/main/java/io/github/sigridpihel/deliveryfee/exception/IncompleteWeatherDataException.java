package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;

public class IncompleteWeatherDataException extends RuntimeException{
    public IncompleteWeatherDataException(String stationName, String missingValue) {
        super(String.format("%s missing for %s", missingValue, stationName));
    }
}

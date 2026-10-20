package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;

public class RegionalBaseFeeNotFoundException extends RuntimeException{
    /**
     * Creates the exception for a city and vehicle type without a configured base fee.
     *
     * @param city    requested city
     * @param vehicle requested vehicle type
     */
    public RegionalBaseFeeNotFoundException(City city, VehicleType vehicle) {
        super(String.format("No regional base fee configured for %s / %s", city, vehicle));
    }
}

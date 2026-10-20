package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;

public class RegionalBaseFeeNotFoundException extends RuntimeException{
    public RegionalBaseFeeNotFoundException(City city, VehicleType vehicle) {
        super(String.format("No regional base fee configured for %s / %s", city, vehicle));
    }
}

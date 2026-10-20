package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;

public class VehicleUsageForbiddenException extends RuntimeException{
    public VehicleUsageForbiddenException() {
        super("Usage of selected vehicle type is forbidden");
    }
}

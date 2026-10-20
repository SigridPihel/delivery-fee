package io.github.sigridpihel.deliveryfee.exception;

public class VehicleUsageForbiddenException extends RuntimeException{
    public VehicleUsageForbiddenException() {
        super("Usage of selected vehicle type is forbidden");
    }
}

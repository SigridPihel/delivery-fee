package io.github.sigridpihel.deliveryfee.exception;

public class VehicleUsageForbiddenException extends RuntimeException{
    /**
     * Creates the exception with the message "Usage of selected vehicle type is forbidden".
     */
    public VehicleUsageForbiddenException() {
        super("Usage of selected vehicle type is forbidden");
    }
}

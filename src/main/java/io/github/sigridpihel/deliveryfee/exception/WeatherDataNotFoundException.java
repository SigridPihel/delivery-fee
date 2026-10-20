package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;

public class WeatherDataNotFoundException extends RuntimeException{
    public WeatherDataNotFoundException(City city) {
        super(String.format("No weather data available for %s", city));
    }
}

package io.github.sigridpihel.deliveryfee.exception;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;

public class WeatherDataNotFoundException extends RuntimeException{
    /**
     * Creates the exception for a city without any imported weather data.
     *
     * @param city city with no weather data
     */
    public WeatherDataNotFoundException(City city) {
        super(String.format("No weather data available for %s", city));
    }
}

package io.github.sigridpihel.deliveryfee.mapper;

import io.github.sigridpihel.deliveryfee.client.dto.Station;
import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WeatherObservationMapper {

    /**
     * Converts a station from the weather API into a new, unsaved entity.
     *
     * @param stationDto           station data from the weather API
     * @param observationTimeStamp observation time in seconds since the Unix epoch
     * @return the weather observation entity
     */
    public WeatherObservation toEntity(Station stationDto, Long observationTimeStamp) {
        WeatherObservation weatherObservation = new WeatherObservation();
        weatherObservation.setStationName(stationDto.stationName());
        weatherObservation.setWmoCode(stationDto.wmoCode());
        weatherObservation.setAirTemperature(stationDto.airTemperature());
        weatherObservation.setWindSpeed(stationDto.windSpeed());
        weatherObservation.setPhenomenon(stationDto.phenomenon());
        weatherObservation.setObservationTimestamp(Instant.ofEpochSecond(observationTimeStamp));

        return weatherObservation;
    }
}

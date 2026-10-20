package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
    /**
     * Finds the most recent observation of a weather station.
     *
     * @param stationName name of the weather station
     * @return the station's most recent observation, if any
     */
    Optional<WeatherObservation> findFirstByStationNameOrderByObservationTimestampDesc(String stationName);
}

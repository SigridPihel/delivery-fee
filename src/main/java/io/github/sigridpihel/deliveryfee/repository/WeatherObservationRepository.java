package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
    Optional<WeatherObservation> findFirstByStationNameOrderByObservationTimestampDesc(String stationName);
}

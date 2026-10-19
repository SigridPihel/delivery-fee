package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
}

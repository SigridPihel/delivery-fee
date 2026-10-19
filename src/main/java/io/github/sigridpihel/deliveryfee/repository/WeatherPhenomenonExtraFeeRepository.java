package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherPhenomenonExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherPhenomenonExtraFeeRepository extends JpaRepository<WeatherPhenomenonExtraFee, Long> {
}

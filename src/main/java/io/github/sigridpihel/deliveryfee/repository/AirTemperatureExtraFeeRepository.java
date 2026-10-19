package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.AirTemperatureExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirTemperatureExtraFeeRepository extends JpaRepository<AirTemperatureExtraFee, Long> {
}

package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WindSpeedExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WindSpeedExtraFeeRepository extends JpaRepository<WindSpeedExtraFee, Long> {
}

package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WindSpeedExtraFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WindSpeedExtraFeeRepository extends JpaRepository<WindSpeedExtraFee, Long> {
    List<WindSpeedExtraFee> findByVehicleType(VehicleType vehicle);
}

package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WindSpeedExtraFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WindSpeedExtraFeeRepository extends JpaRepository<WindSpeedExtraFee, Long> {
    /**
     * Finds the wind speed extra fee rules for a vehicle type.
     *
     * @param vehicle vehicle type
     * @return all wind speed extra fee rules for the vehicle type; empty if none
     */
    List<WindSpeedExtraFee> findByVehicleType(VehicleType vehicle);
}

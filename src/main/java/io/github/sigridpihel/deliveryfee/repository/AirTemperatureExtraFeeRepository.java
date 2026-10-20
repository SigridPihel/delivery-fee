package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.AirTemperatureExtraFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirTemperatureExtraFeeRepository extends JpaRepository<AirTemperatureExtraFee, Long> {
    /**
     * Finds the air temperature extra fee rules for a vehicle type.
     *
     * @param vehicle vehicle type
     * @return all air temperature extra fee rules for the vehicle type; empty if none
     */
    List<AirTemperatureExtraFee> findByVehicleType(VehicleType vehicle);
}

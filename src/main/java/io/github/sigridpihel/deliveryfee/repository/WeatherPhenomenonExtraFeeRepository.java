package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherPhenomenonExtraFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherPhenomenonExtraFeeRepository extends JpaRepository<WeatherPhenomenonExtraFee, Long> {
    /**
     * Finds the weather phenomenon extra fee rules for a vehicle type.
     *
     * @param vehicle vehicle type
     * @return all weather phenomenon extra fee rules for the vehicle type; empty if none
     */
    List<WeatherPhenomenonExtraFee> findByVehicleType(VehicleType vehicle);
}

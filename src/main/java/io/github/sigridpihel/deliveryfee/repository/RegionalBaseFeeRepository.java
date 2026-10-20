package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.RegionalBaseFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {

    /**
     * Finds the regional base fee for a city and vehicle type.
     *
     * @param city    city
     * @param vehicle vehicle type
     * @return the regional base fee for the city and vehicle type, if configured
     */
    Optional<RegionalBaseFee> findByCityAndVehicleType(City city, VehicleType vehicle);
}

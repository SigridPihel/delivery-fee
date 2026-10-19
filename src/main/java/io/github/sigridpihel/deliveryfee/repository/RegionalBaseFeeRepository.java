package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.RegionalBaseFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {
}

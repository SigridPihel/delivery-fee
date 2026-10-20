package io.github.sigridpihel.deliveryfee.model.entity;

import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class WeatherPhenomenonExtraFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private String phenomenon;

    private BigDecimal fee;
}

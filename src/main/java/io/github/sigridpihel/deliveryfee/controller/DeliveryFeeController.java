package io.github.sigridpihel.deliveryfee.controller;

import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.service.DeliveryFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    @GetMapping
    public BigDecimal getDeliveryFee(@RequestParam("city") City city, @RequestParam("vehicleType") VehicleType vehicleType) {
        return deliveryFeeService.calculateTotalDeliveryFee(city, vehicleType);
    }
}

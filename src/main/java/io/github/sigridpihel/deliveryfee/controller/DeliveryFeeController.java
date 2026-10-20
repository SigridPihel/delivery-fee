package io.github.sigridpihel.deliveryfee.controller;

import io.github.sigridpihel.deliveryfee.model.dto.DeliveryFeeResponse;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.service.DeliveryFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    /**
     * Returns the total delivery fee for the given city and vehicle type,
     * based on the latest weather observation for that city.
     *
     * @param city        city where the delivery takes place
     * @param vehicleType vehicle used by the courier
     * @return the total delivery fee in euros
     */
    @GetMapping
    public DeliveryFeeResponse getDeliveryFee(@RequestParam("city") City city, @RequestParam("vehicleType") VehicleType vehicleType) {
        return deliveryFeeService.calculateTotalDeliveryFee(city, vehicleType);
    }
}

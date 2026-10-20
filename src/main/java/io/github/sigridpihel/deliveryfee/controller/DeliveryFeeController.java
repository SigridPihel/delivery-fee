package io.github.sigridpihel.deliveryfee.controller;

import io.github.sigridpihel.deliveryfee.exception.ErrorResponse;
import io.github.sigridpihel.deliveryfee.model.dto.DeliveryFeeResponse;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("delivery-fee")
@Tag(name = "Delivery fee", description = "Calculation of courier delivery fees")
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
    @Operation(
            summary = "Calculate the delivery fee",
            description = "Returns the total delivery fee: the regional base fee for the city and vehicle type, "
                    + "plus extra fees for air temperature, wind speed and weather phenomenon. "
                    + "Extra fees are based on the latest imported weather observation for the city.")
    @ApiResponse(responseCode = "200", description = "Delivery fee calculated")
    @ApiResponse(responseCode = "400", description = "A parameter is missing or has an invalid value",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "Usage of selected vehicle type is forbidden "
            + "(wind speed over 20 m/s, or glaze, hail or thunder)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "No weather data available for the city, "
            + "or a weather value needed for the calculation is missing",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Fee configuration missing or unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public DeliveryFeeResponse getDeliveryFee(
            @Parameter(description = "City where the delivery takes place", example = "TARTU")
            @RequestParam("city") City city,
            @Parameter(description = "Vehicle used by the courier", example = "BIKE")
            @RequestParam("vehicleType") VehicleType vehicleType) {
        return deliveryFeeService.calculateTotalDeliveryFee(city, vehicleType);
    }
}

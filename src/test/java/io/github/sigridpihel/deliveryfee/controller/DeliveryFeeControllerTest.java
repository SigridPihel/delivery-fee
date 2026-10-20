package io.github.sigridpihel.deliveryfee.controller;

import io.github.sigridpihel.deliveryfee.exception.IncompleteWeatherDataException;
import io.github.sigridpihel.deliveryfee.exception.RegionalBaseFeeNotFoundException;
import io.github.sigridpihel.deliveryfee.exception.VehicleUsageForbiddenException;
import io.github.sigridpihel.deliveryfee.exception.WeatherDataNotFoundException;
import io.github.sigridpihel.deliveryfee.model.dto.DeliveryFeeResponse;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.service.DeliveryFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the HTTP layer: parameter binding, the JSON response and the mapping of
 * exceptions to status codes by the global exception handler.
 */
@WebMvcTest(DeliveryFeeController.class)
class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryFeeService deliveryFeeService;

    @Test
    void returnsDeliveryFee() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, VehicleType.BIKE))
                .thenReturn(new DeliveryFeeResponse(new BigDecimal("4.00")));

        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "BIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(4.00));
    }

    @Test
    void returns400ForUnknownCity() throws Exception {
        mockMvc.perform(get("/delivery-fee").param("city", "NARVA").param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value 'NARVA' for parameter 'city'"));

        verifyNoInteractions(deliveryFeeService);
    }

    @Test
    void returns400ForUnknownVehicleType() throws Exception {
        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "PLANE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value 'PLANE' for parameter 'vehicleType'"));
    }

    @Test
    void returns400WhenParameterIsMissing() throws Exception {
        mockMvc.perform(get("/delivery-fee").param("city", "TARTU"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required query parameter 'vehicleType' is missing"));
    }

    @Test
    void returns422WhenVehicleUsageIsForbidden() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, VehicleType.BIKE))
                .thenThrow(new VehicleUsageForbiddenException());

        mockMvc.perform(get("/delivery-fee").param("city", "TALLINN").param("vehicleType", "BIKE"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Usage of selected vehicle type is forbidden"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returns503WhenNoWeatherDataExists() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, VehicleType.CAR))
                .thenThrow(new WeatherDataNotFoundException(City.TARTU));

        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "CAR"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("No weather data available for TARTU"));
    }

    @Test
    void returns503WhenWeatherDataIsIncomplete() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, VehicleType.BIKE))
                .thenThrow(new IncompleteWeatherDataException("Tartu-Tõravere", "Wind speed"));

        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "BIKE"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Wind speed missing for Tartu-Tõravere"));
    }

    @Test
    void returns500WhenBaseFeeIsNotConfigured() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, VehicleType.CAR))
                .thenThrow(new RegionalBaseFeeNotFoundException(City.TARTU, VehicleType.CAR));

        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "CAR"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void returns500WithGenericMessageForUnexpectedErrors() throws Exception {
        when(deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, VehicleType.CAR))
                .thenThrow(new IllegalStateException("database exploded"));

        mockMvc.perform(get("/delivery-fee").param("city", "TARTU").param("vehicleType", "CAR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}

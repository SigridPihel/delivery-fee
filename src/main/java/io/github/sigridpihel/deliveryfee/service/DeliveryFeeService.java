package io.github.sigridpihel.deliveryfee.service;

import io.github.sigridpihel.deliveryfee.exception.IncompleteWeatherDataException;
import io.github.sigridpihel.deliveryfee.exception.RegionalBaseFeeNotFoundException;
import io.github.sigridpihel.deliveryfee.exception.VehicleUsageForbiddenException;
import io.github.sigridpihel.deliveryfee.exception.WeatherDataNotFoundException;
import io.github.sigridpihel.deliveryfee.model.entity.AirTemperatureExtraFee;
import io.github.sigridpihel.deliveryfee.model.entity.RegionalBaseFee;
import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import io.github.sigridpihel.deliveryfee.model.entity.WindSpeedExtraFee;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.repository.AirTemperatureExtraFeeRepository;
import io.github.sigridpihel.deliveryfee.repository.RegionalBaseFeeRepository;
import io.github.sigridpihel.deliveryfee.repository.WeatherObservationRepository;
import io.github.sigridpihel.deliveryfee.repository.WindSpeedExtraFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryFeeService {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;
    private final WeatherObservationRepository weatherObservationRepository;
    private final AirTemperatureExtraFeeRepository airTemperatureExtraFeeRepository;
    private final WindSpeedExtraFeeRepository windSpeedExtraFeeRepository;

    private static final BigDecimal MAX_ALLOWED_WIND_SPEED = new BigDecimal("20");

    public BigDecimal calculateTotalDeliveryFee(City city, VehicleType vehicle) {
        WeatherObservation weatherObservation = weatherObservationRepository
                .findFirstByStationNameOrderByObservationTimestampDesc(city.getStationName())
                .orElseThrow(() -> new WeatherDataNotFoundException(city));

        BigDecimal regionalBaseFee = getRegionalBaseFee(city, vehicle);
        BigDecimal airTemperatureExtraFee = getAirTemperatureExtraFee(vehicle, weatherObservation);
        BigDecimal windSpeedExtraFee = getWindSpeedExtraFee(vehicle, weatherObservation);

        // create a function that calculates all fees together!
        BigDecimal totalFee = regionalBaseFee.add(airTemperatureExtraFee).add(windSpeedExtraFee);

        return totalFee;
    }

    private BigDecimal getWindSpeedExtraFee(VehicleType vehicle, WeatherObservation weatherObservation) {
        List<WindSpeedExtraFee> rules = windSpeedExtraFeeRepository
                .findByVehicleType(vehicle);

        if (rules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (weatherObservation.getWindSpeed() == null) {
            throw new IncompleteWeatherDataException(weatherObservation.getStationName(), "Wind speed");
        }

        if (weatherObservation.getWindSpeed().compareTo(MAX_ALLOWED_WIND_SPEED) > 0) {
            throw new VehicleUsageForbiddenException();
        }

        for (WindSpeedExtraFee rule : rules) {
            BigDecimal min = rule.getMinWindSpeed();
            BigDecimal max = rule.getMaxWindSpeed();

            boolean inRange = isInRange(weatherObservation.getWindSpeed(), min, max);

            if (inRange) {
                return rule.getFee();
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getAirTemperatureExtraFee(VehicleType vehicle, WeatherObservation weatherObservation) {
        List<AirTemperatureExtraFee> rules = airTemperatureExtraFeeRepository
                .findByVehicleType(vehicle);

        if (rules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (weatherObservation.getAirTemperature() == null) {
            throw new IncompleteWeatherDataException(weatherObservation.getStationName(), "Air temperature");
        }

        for (AirTemperatureExtraFee rule : rules) {
            BigDecimal min = rule.getMinAirTemperature();
            BigDecimal max = rule.getMaxAirTemperature();

            boolean inRange = isInRange(weatherObservation.getAirTemperature(), min, max);

            if (inRange) {
                return rule.getFee();
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getRegionalBaseFee(City city, VehicleType vehicle) {
        RegionalBaseFee regionalBaseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicle)
                .orElseThrow(() -> new RegionalBaseFeeNotFoundException(city, vehicle));

        return regionalBaseFee.getFee();
    }

    private boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (min != null && max != null) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }
        if (max != null) {
            return value.compareTo(max) < 0;
        }
        return value.compareTo(min) > 0;
    }
}

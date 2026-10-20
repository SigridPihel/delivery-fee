package io.github.sigridpihel.deliveryfee.service;

import io.github.sigridpihel.deliveryfee.exception.IncompleteWeatherDataException;
import io.github.sigridpihel.deliveryfee.exception.RegionalBaseFeeNotFoundException;
import io.github.sigridpihel.deliveryfee.exception.VehicleUsageForbiddenException;
import io.github.sigridpihel.deliveryfee.exception.WeatherDataNotFoundException;
import io.github.sigridpihel.deliveryfee.model.entity.*;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType;
import io.github.sigridpihel.deliveryfee.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.sigridpihel.deliveryfee.model.enumeration.VehicleType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DeliveryFeeService}. The repositories are mocked and return the same
 * rules as the Liquibase seed data, so the tests check the business rules from the task.
 */
@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {

    private static final String SEED_SNOW_OR_SLEET = "Light snow shower";
    private static final String SEED_RAIN = "Light rain";
    private static final String NO_PHENOMENON = "Clear";

    @Mock
    private RegionalBaseFeeRepository regionalBaseFeeRepository;
    @Mock
    private WeatherObservationRepository weatherObservationRepository;
    @Mock
    private AirTemperatureExtraFeeRepository airTemperatureExtraFeeRepository;
    @Mock
    private WindSpeedExtraFeeRepository windSpeedExtraFeeRepository;
    @Mock
    private WeatherPhenomenonExtraFeeRepository weatherPhenomenonExtraFeeRepository;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    void setUpSeedRules() {
        Map<City, Map<VehicleType, String>> baseFees = Map.of(
                City.TALLINN, Map.of(CAR, "4", SCOOTER, "3.5", BIKE, "3"),
                City.TARTU, Map.of(CAR, "3.5", SCOOTER, "3", BIKE, "2.5"),
                City.PÄRNU, Map.of(CAR, "3", SCOOTER, "2.5", BIKE, "2"));
        lenient().when(regionalBaseFeeRepository.findByCityAndVehicleType(any(), any()))
                .thenAnswer(invocation -> {
                    City city = invocation.getArgument(0);
                    VehicleType vehicle = invocation.getArgument(1);
                    return Optional.of(baseFee(city, vehicle, baseFees.get(city).get(vehicle)));
                });

        lenient().when(airTemperatureExtraFeeRepository.findByVehicleType(any()))
                .thenAnswer(invocation -> {
                    VehicleType vehicle = invocation.getArgument(0);
                    if (vehicle == CAR) {
                        return List.of();
                    }
                    return List.of(
                            airTemperatureRule(vehicle, null, "-10", "1"),
                            airTemperatureRule(vehicle, "-10", "0", "0.5"));
                });

        lenient().when(windSpeedExtraFeeRepository.findByVehicleType(any()))
                .thenAnswer(invocation -> {
                    VehicleType vehicle = invocation.getArgument(0);
                    return vehicle == BIKE ? List.of(windSpeedRule(vehicle, "10", "20", "0.5")) : List.of();
                });

        lenient().when(weatherPhenomenonExtraFeeRepository.findByVehicleType(any()))
                .thenAnswer(invocation -> {
                    VehicleType vehicle = invocation.getArgument(0);
                    if (vehicle == CAR) {
                        return List.of();
                    }
                    return List.of(
                            phenomenonRule(vehicle, SEED_SNOW_OR_SLEET, "1"),
                            phenomenonRule(vehicle, "Moderate sleet", "1"),
                            phenomenonRule(vehicle, SEED_RAIN, "0.5"),
                            phenomenonRule(vehicle, "Heavy shower", "0.5"));
                });
    }

    // --- positive cases ---

    @Test
    void calculatesTaskExampleForTartuBike() {
        givenLatestWeather(City.TARTU, "-2.1", "4.7", SEED_SNOW_OR_SLEET);

        assertFee(City.TARTU, BIKE, "4.00");
    }

    @ParameterizedTest
    @CsvSource({
            "TALLINN, CAR, 4", "TALLINN, SCOOTER, 3.5", "TALLINN, BIKE, 3",
            "TARTU, CAR, 3.5", "TARTU, SCOOTER, 3", "TARTU, BIKE, 2.5",
            "PÄRNU, CAR, 3", "PÄRNU, SCOOTER, 2.5", "PÄRNU, BIKE, 2"})
    void returnsOnlyRegionalBaseFeeInMildWeather(City city, VehicleType vehicle, String expectedFee) {
        givenLatestWeather(city, "15", "3", NO_PHENOMENON);

        assertFee(city, vehicle, expectedFee);
    }

    @Test
    void carPaysNoExtraFeesEvenInExtremeWeather() {
        givenLatestWeather(City.TALLINN, "-25", "30", "Hail");

        assertFee(City.TALLINN, CAR, "4");
    }

    @Test
    void scooterPaysNoWindSpeedExtraFee() {
        givenLatestWeather(City.TALLINN, "15", "15", NO_PHENOMENON);

        assertFee(City.TALLINN, SCOOTER, "3.5");
    }

    @Test
    void scooterIsNotForbiddenByStrongWind() {
        givenLatestWeather(City.TALLINN, "15", "25", NO_PHENOMENON);

        assertFee(City.TALLINN, SCOOTER, "3.5");
    }

    @Test
    void addsAllExtraFeesTogether() {
        givenLatestWeather(City.TALLINN, "-15", "15", SEED_SNOW_OR_SLEET);

        // 3 base + 1 temperature + 0.5 wind + 1 phenomenon
        assertFee(City.TALLINN, BIKE, "5.5");
    }

    // --- edge cases: range boundaries ---

    @ParameterizedTest(name = "air temperature {0} °C -> total {1}")
    @CsvSource({
            "-10.1, 4",   // less than -10 -> 1 €
            "-10,   3.5", // -10 belongs to "between -10 and 0" -> 0.5 €
            "-5,    3.5",
            "0,     3.5", // 0 is included in "between -10 and 0"
            "0.1,   3"})  // above 0 -> no extra fee
    void appliesAirTemperatureBoundaries(String airTemperature, String expectedFee) {
        givenLatestWeather(City.TALLINN, airTemperature, "0", NO_PHENOMENON);

        assertFee(City.TALLINN, BIKE, expectedFee);
    }

    @ParameterizedTest(name = "wind speed {0} m/s -> total {1}")
    @CsvSource({
            "9.9,  3",
            "10,   3.5", // 10 is included in "between 10 and 20"
            "15,   3.5",
            "20,   3.5"}) // 20 is included; only greater than 20 is forbidden
    void appliesWindSpeedBoundaries(String windSpeed, String expectedFee) {
        givenLatestWeather(City.TALLINN, "15", windSpeed, NO_PHENOMENON);

        assertFee(City.TALLINN, BIKE, expectedFee);
    }

    @ParameterizedTest(name = "phenomenon {0} -> total {1}")
    @CsvSource({
            "Light snow shower, 4",
            "Moderate sleet,    4",
            "Light rain,        3.5",
            "Heavy shower,      3.5",
            "Clear,             3",
            "Overcast,          3"})
    void appliesWeatherPhenomenonFees(String phenomenon, String expectedFee) {
        givenLatestWeather(City.TALLINN, "15", "0", phenomenon);

        assertFee(City.TALLINN, BIKE, expectedFee);
    }

    // --- negative cases: forbidden conditions ---

    @Test
    void forbidsBikeWhenWindSpeedIsAbove20() {
        givenLatestWeather(City.TALLINN, "15", "20.1", NO_PHENOMENON);

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, BIKE))
                .isInstanceOf(VehicleUsageForbiddenException.class)
                .hasMessage("Usage of selected vehicle type is forbidden");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Glaze", "Hail", "Thunder"})
    void forbidsScooterInDangerousWeatherPhenomena(String phenomenon) {
        givenLatestWeather(City.TARTU, "15", "0", phenomenon);

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, SCOOTER))
                .isInstanceOf(VehicleUsageForbiddenException.class)
                .hasMessage("Usage of selected vehicle type is forbidden");
    }

    // --- negative cases: missing data ---

    @Test
    void throwsWhenNoWeatherDataExistsForCity() {
        when(weatherObservationRepository.findFirstByStationNameOrderByObservationTimestampDesc("Pärnu"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.PÄRNU, CAR))
                .isInstanceOf(WeatherDataNotFoundException.class);
    }

    @Test
    void throwsWhenRegionalBaseFeeIsNotConfigured() {
        givenLatestWeather(City.TALLINN, "15", "0", NO_PHENOMENON);
        when(regionalBaseFeeRepository.findByCityAndVehicleType(City.TALLINN, CAR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, CAR))
                .isInstanceOf(RegionalBaseFeeNotFoundException.class);
    }

    @Test
    void throwsWhenAirTemperatureIsMissingForBike() {
        givenLatestWeather(City.TALLINN, null, "0", NO_PHENOMENON);

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, BIKE))
                .isInstanceOf(IncompleteWeatherDataException.class)
                .hasMessageContaining("Air temperature");
    }

    @Test
    void throwsWhenWindSpeedIsMissingForBike() {
        givenLatestWeather(City.TALLINN, "15", null, NO_PHENOMENON);

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, BIKE))
                .isInstanceOf(IncompleteWeatherDataException.class)
                .hasMessageContaining("Wind speed");
    }

    @Test
    void throwsWhenWeatherPhenomenonIsMissingForScooter() {
        givenLatestWeather(City.TALLINN, "15", "0", null);

        assertThatThrownBy(() -> deliveryFeeService.calculateTotalDeliveryFee(City.TALLINN, SCOOTER))
                .isInstanceOf(IncompleteWeatherDataException.class)
                .hasMessageContaining("Weather phenomenon");
    }

    @Test
    void carFeeIsCalculatedEvenWhenWeatherValuesAreMissing() {
        givenLatestWeather(City.TALLINN, null, null, null);

        assertFee(City.TALLINN, CAR, "4");
    }

    // --- latest data ---

    @Test
    void usesLatestObservationOfTheCitysStation() {
        givenLatestWeather(City.TARTU, "15", "0", NO_PHENOMENON);

        deliveryFeeService.calculateTotalDeliveryFee(City.TARTU, CAR);

        verify(weatherObservationRepository).findFirstByStationNameOrderByObservationTimestampDesc("Tartu-Tõravere");
    }

    // --- helpers ---

    private void assertFee(City city, VehicleType vehicle, String expectedFee) {
        BigDecimal fee = deliveryFeeService.calculateTotalDeliveryFee(city, vehicle).fee();
        assertThat(fee).isEqualByComparingTo(expectedFee);
    }

    private void givenLatestWeather(City city, String airTemperature, String windSpeed, String phenomenon) {
        WeatherObservation observation = new WeatherObservation();
        observation.setStationName(city.getStationName());
        observation.setAirTemperature(decimal(airTemperature));
        observation.setWindSpeed(decimal(windSpeed));
        observation.setPhenomenon(phenomenon);
        observation.setObservationTimestamp(Instant.parse("2026-10-20T13:15:00Z"));
        when(weatherObservationRepository.findFirstByStationNameOrderByObservationTimestampDesc(city.getStationName()))
                .thenReturn(Optional.of(observation));
    }

    private static BigDecimal decimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    private static RegionalBaseFee baseFee(City city, VehicleType vehicle, String fee) {
        RegionalBaseFee baseFee = new RegionalBaseFee();
        baseFee.setCity(city);
        baseFee.setVehicleType(vehicle);
        baseFee.setFee(new BigDecimal(fee));
        return baseFee;
    }

    private static AirTemperatureExtraFee airTemperatureRule(VehicleType vehicle, String min, String max, String fee) {
        AirTemperatureExtraFee rule = new AirTemperatureExtraFee();
        rule.setVehicleType(vehicle);
        rule.setMinAirTemperature(decimal(min));
        rule.setMaxAirTemperature(decimal(max));
        rule.setFee(new BigDecimal(fee));
        return rule;
    }

    private static WindSpeedExtraFee windSpeedRule(VehicleType vehicle, String min, String max, String fee) {
        WindSpeedExtraFee rule = new WindSpeedExtraFee();
        rule.setVehicleType(vehicle);
        rule.setMinWindSpeed(decimal(min));
        rule.setMaxWindSpeed(decimal(max));
        rule.setFee(new BigDecimal(fee));
        return rule;
    }

    private static WeatherPhenomenonExtraFee phenomenonRule(VehicleType vehicle, String phenomenon, String fee) {
        WeatherPhenomenonExtraFee rule = new WeatherPhenomenonExtraFee();
        rule.setVehicleType(vehicle);
        rule.setPhenomenon(phenomenon);
        rule.setFee(new BigDecimal(fee));
        return rule;
    }
}

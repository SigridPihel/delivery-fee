package io.github.sigridpihel.deliveryfee.mapper;

import io.github.sigridpihel.deliveryfee.client.dto.Station;
import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherObservationMapperTest {

    private final WeatherObservationMapper mapper = new WeatherObservationMapper();

    @Test
    void copiesAllStationFields() {
        Station station = new Station("Tartu-Tõravere", "26242",
                new BigDecimal("-2.1"), new BigDecimal("4.7"), "Light snow shower");

        WeatherObservation observation = mapper.toEntity(station, 1792474102L);

        assertThat(observation.getId()).isNull();
        assertThat(observation.getStationName()).isEqualTo("Tartu-Tõravere");
        assertThat(observation.getWmoCode()).isEqualTo("26242");
        assertThat(observation.getAirTemperature()).isEqualByComparingTo("-2.1");
        assertThat(observation.getWindSpeed()).isEqualByComparingTo("4.7");
        assertThat(observation.getPhenomenon()).isEqualTo("Light snow shower");
    }

    @Test
    void convertsUnixSecondsToInstant() {
        Station station = new Station("Pärnu", "41803", BigDecimal.ONE, BigDecimal.ONE, "Clear");

        WeatherObservation observation = mapper.toEntity(station, 1792474102L);

        // seconds, not milliseconds: milliseconds would give a date in January 1970
        assertThat(observation.getObservationTimestamp()).isEqualTo(Instant.parse("2026-10-20T05:28:22Z"));
    }

    @Test
    void keepsMissingWeatherValuesAsNull() {
        Station station = new Station("Pärnu", "41803", null, null, null);

        WeatherObservation observation = mapper.toEntity(station, 1792474102L);

        assertThat(observation.getAirTemperature()).isNull();
        assertThat(observation.getWindSpeed()).isNull();
        assertThat(observation.getPhenomenon()).isNull();
    }
}

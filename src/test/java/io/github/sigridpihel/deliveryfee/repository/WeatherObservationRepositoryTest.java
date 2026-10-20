package io.github.sigridpihel.deliveryfee.repository;

import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks against a real H2 database (schema created by Liquibase) that the newest
 * observation of a station is the one used for fee calculations.
 */
@DataJpaTest
class WeatherObservationRepositoryTest {

    @Autowired
    private WeatherObservationRepository repository;

    @Test
    void returnsNewestObservationOfTheStation() {
        save("Tartu-Tõravere", "2026-10-20T11:15:00Z", "1.0");
        save("Tartu-Tõravere", "2026-10-20T13:15:00Z", "3.0");
        save("Tartu-Tõravere", "2026-10-20T12:15:00Z", "2.0");

        Optional<WeatherObservation> latest =
                repository.findFirstByStationNameOrderByObservationTimestampDesc("Tartu-Tõravere");

        assertThat(latest).isPresent();
        assertThat(latest.get().getObservationTimestamp()).isEqualTo(Instant.parse("2026-10-20T13:15:00Z"));
        assertThat(latest.get().getAirTemperature()).isEqualByComparingTo("3.0");
    }

    @Test
    void ignoresNewerObservationsOfOtherStations() {
        save("Tartu-Tõravere", "2026-10-20T12:15:00Z", "2.0");
        save("Tallinn-Harku", "2026-10-20T14:15:00Z", "5.0");

        Optional<WeatherObservation> latest =
                repository.findFirstByStationNameOrderByObservationTimestampDesc("Tartu-Tõravere");

        assertThat(latest).isPresent();
        assertThat(latest.get().getStationName()).isEqualTo("Tartu-Tõravere");
        assertThat(latest.get().getAirTemperature()).isEqualByComparingTo("2.0");
    }

    @Test
    void returnsEmptyWhenStationHasNoObservations() {
        save("Tallinn-Harku", "2026-10-20T12:15:00Z", "5.0");

        assertThat(repository.findFirstByStationNameOrderByObservationTimestampDesc("Pärnu")).isEmpty();
    }

    @Test
    void keepsHistoryOfObservationsForTheSameStation() {
        save("Pärnu", "2026-10-20T12:15:00Z", "4.0");
        save("Pärnu", "2026-10-20T13:15:00Z", "4.5");

        assertThat(repository.count()).isEqualTo(2);
    }

    private void save(String stationName, String timestamp, String airTemperature) {
        WeatherObservation observation = new WeatherObservation();
        observation.setStationName(stationName);
        observation.setWmoCode("26242");
        observation.setAirTemperature(new BigDecimal(airTemperature));
        observation.setWindSpeed(new BigDecimal("3.0"));
        observation.setPhenomenon("Clear");
        observation.setObservationTimestamp(Instant.parse(timestamp));
        repository.save(observation);
    }
}

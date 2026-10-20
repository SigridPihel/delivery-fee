package io.github.sigridpihel.deliveryfee.service;

import io.github.sigridpihel.deliveryfee.client.WeatherApiClient;
import io.github.sigridpihel.deliveryfee.client.dto.Observations;
import io.github.sigridpihel.deliveryfee.client.dto.Station;
import io.github.sigridpihel.deliveryfee.mapper.WeatherObservationMapper;
import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import io.github.sigridpihel.deliveryfee.repository.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherImportServiceTest {

    private static final long TIMESTAMP = 1792474102L;

    @Mock
    private WeatherApiClient weatherApiClient;
    @Mock
    private WeatherObservationRepository weatherObservationRepository;
    @Captor
    private ArgumentCaptor<List<WeatherObservation>> savedCaptor;

    private WeatherImportService weatherImportService;

    @BeforeEach
    void setUp() {
        weatherImportService = new WeatherImportService(
                weatherApiClient, new WeatherObservationMapper(), weatherObservationRepository);
    }

    @Test
    void savesOnlyTrackedStations() {
        givenApiReturns(
                station("Tallinn-Harku"),
                station("Narva"),
                station("Tartu-Tõravere"),
                station("Kuressaare linn"),
                station("Pärnu"));

        weatherImportService.importObservations();

        verify(weatherObservationRepository).saveAll(savedCaptor.capture());
        assertThat(savedCaptor.getValue())
                .extracting(WeatherObservation::getStationName)
                .containsExactlyInAnyOrder("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");
    }

    @Test
    void savesNewObservationsWithTheImportTimestamp() {
        givenApiReturns(station("Tallinn-Harku"), station("Pärnu"));

        weatherImportService.importObservations();

        verify(weatherObservationRepository).saveAll(savedCaptor.capture());
        assertThat(savedCaptor.getValue()).allSatisfy(observation -> {
            assertThat(observation.getId()).isNull(); // new rows, existing ones are not overwritten
            assertThat(observation.getObservationTimestamp()).isEqualTo(Instant.ofEpochSecond(TIMESTAMP));
        });
    }

    @Test
    void savesNothingWhenNoTrackedStationIsInTheResponse() {
        givenApiReturns(station("Narva"));

        weatherImportService.importObservations();

        verify(weatherObservationRepository).saveAll(savedCaptor.capture());
        assertThat(savedCaptor.getValue()).isEmpty();
    }

    @Test
    void savesNothingWhenWeatherApiFails() {
        when(weatherApiClient.fetchObservations()).thenThrow(new RestClientException("Connection refused"));

        assertThatThrownBy(() -> weatherImportService.importObservations())
                .isInstanceOf(RestClientException.class);
        verifyNoInteractions(weatherObservationRepository);
    }

    private void givenApiReturns(Station... stations) {
        when(weatherApiClient.fetchObservations()).thenReturn(new Observations(TIMESTAMP, List.of(stations)));
    }

    private static Station station(String name) {
        return new Station(name, "26038", new BigDecimal("-2.1"), new BigDecimal("4.7"), "Light snow shower");
    }
}

package io.github.sigridpihel.deliveryfee.service;

import io.github.sigridpihel.deliveryfee.client.WeatherApiClient;
import io.github.sigridpihel.deliveryfee.client.dto.Observations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WeatherImportService {

    private final WeatherApiClient weatherApiClient;

    public void importObservations() {
        Observations observations = weatherApiClient.fetchObservations();
        System.out.println(observations.timestamp());
        System.out.println(observations.stations());
    }
}

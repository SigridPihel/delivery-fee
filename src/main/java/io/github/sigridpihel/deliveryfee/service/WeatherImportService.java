package io.github.sigridpihel.deliveryfee.service;

import io.github.sigridpihel.deliveryfee.client.WeatherApiClient;
import io.github.sigridpihel.deliveryfee.client.dto.Observations;
import io.github.sigridpihel.deliveryfee.client.dto.Station;
import io.github.sigridpihel.deliveryfee.mapper.WeatherObservationMapper;
import io.github.sigridpihel.deliveryfee.model.entity.WeatherObservation;
import io.github.sigridpihel.deliveryfee.model.enumeration.City;
import io.github.sigridpihel.deliveryfee.repository.WeatherObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class WeatherImportService {

    private final WeatherApiClient weatherApiClient;
    private static final Set<String> TRACKED_STATIONS = Set.of(City.PÄRNU.getStationName(), City.TALLINN.getStationName(), City.TARTU.getStationName());
    private final WeatherObservationMapper weatherObservationMapper;
    private final WeatherObservationRepository weatherObservationRepository;

    public void importObservations() {
        Observations observations = weatherApiClient.fetchObservations();
        List<Station> trackedStations = observations.stations()
                .stream()
                .filter(station -> TRACKED_STATIONS.contains(station.stationName()))
                .toList();
        List<WeatherObservation> weatherObservations = trackedStations.stream()
                .map(station -> weatherObservationMapper.toEntity(station, observations.timestamp()))
                .toList();
        weatherObservationRepository.saveAll(weatherObservations);
    }
}

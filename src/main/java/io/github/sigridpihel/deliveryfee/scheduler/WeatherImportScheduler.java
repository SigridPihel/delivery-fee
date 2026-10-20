package io.github.sigridpihel.deliveryfee.scheduler;

import io.github.sigridpihel.deliveryfee.service.WeatherImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WeatherImportScheduler {

    private final WeatherImportService weatherImportService;

    /**
     * Imports weather observations on the schedule set by {@code weather.import.cron}
     * (by default every hour at HH:15:00).
     */
    @Scheduled(cron = "${weather.import.cron}")
    public void importOnSchedule(){
        weatherImportService.importObservations();
    }

    /**
     * Imports weather observations once when the application has started,
     * so current data is available without waiting for the first scheduled run.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void importOnStartup(){
        weatherImportService.importObservations();
    }
}

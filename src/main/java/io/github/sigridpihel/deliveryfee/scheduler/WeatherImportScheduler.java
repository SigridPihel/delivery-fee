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

    @Scheduled(cron = "${weather.import.cron}")
    public void importOnSchedule(){
        weatherImportService.importObservations();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void importOnStartup(){
        weatherImportService.importObservations();
    }
}

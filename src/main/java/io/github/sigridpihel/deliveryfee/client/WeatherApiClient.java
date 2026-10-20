package io.github.sigridpihel.deliveryfee.client;

import io.github.sigridpihel.deliveryfee.client.dto.Observations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;

    /**
     * Creates the client using Spring Boot's preconfigured {@link RestClient.Builder}.
     *
     * @param builder builder provided by Spring Boot
     * @param url     URL of the weather observations XML, from {@code weather.api.url}
     */
    public WeatherApiClient(RestClient.Builder builder,
                            @Value("${weather.api.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    /**
     * Requests the latest observations of all stations from the weather API.
     *
     * @return the parsed observations
     */
    public Observations fetchObservations() {
        return restClient.get().retrieve().body(Observations.class);
    }
}

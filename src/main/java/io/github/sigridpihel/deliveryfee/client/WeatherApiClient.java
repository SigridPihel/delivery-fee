package io.github.sigridpihel.deliveryfee.client;

import io.github.sigridpihel.deliveryfee.client.dto.Observations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;

    public WeatherApiClient(RestClient.Builder builder,
                            @Value("${weather.api.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public Observations fetchObservations() {
        return restClient.get().retrieve().body(Observations.class);
    }
}

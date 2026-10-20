package io.github.sigridpihel.deliveryfee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Provides the title and description shown at the top of the Swagger UI page.
     *
     * @return the OpenAPI definition of this application
     */
    @Bean
    public OpenAPI deliveryFeeOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Delivery Fee API")
                .version("1.0")
                .description("Calculates food courier delivery fees based on city, vehicle type "
                        + "and the latest weather data from the Estonian Environment Agency."));
    }
}

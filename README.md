# Delivery Fee

Spring Boot application that calculates food courier delivery fees based on the city, the
vehicle type and the latest weather conditions. Weather data is imported from the
[Estonian Environment Agency](https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php).

## Running

Requirements: Java 25.

```bash
./gradlew bootRun
```

The application starts on port `8081` with the context path `/api`. On startup it imports the
current weather data once, then on the configured schedule.

## API documentation

Swagger UI: http://localhost:8081/api/swagger-ui.html
OpenAPI JSON: http://localhost:8081/api/v3/api-docs

### Example

```bash
curl "http://localhost:8081/api/delivery-fee?city=TARTU&vehicleType=BIKE"
```

```json
{ "fee": 4.00 }
```

| Parameter     | Required | Values                       |
|---------------|----------|------------------------------|
| `city`        | yes      | `TALLINN`, `TARTU`, `PÄRNU`  |
| `vehicleType` | yes      | `CAR`, `SCOOTER`, `BIKE`     |

### Errors

Errors are returned as JSON with a `message` and a `timestamp`.

| Status | When                                                                       |
|--------|----------------------------------------------------------------------------|
| 400    | A parameter is missing or has an invalid value                             |
| 422    | Usage of selected vehicle type is forbidden                                |
| 503    | No weather data for the city, or a weather value needed for the fee is missing |
| 500    | Fee configuration missing or unexpected server error                       |

## Configuration

Set in `src/main/resources/application.properties`:

| Property              | Default                                                         | Meaning                       |
|-----------------------|-----------------------------------------------------------------|-------------------------------|
| `weather.import.cron` | `0 15 * * * *` (every hour at HH:15:00)                         | Weather import schedule       |
| `weather.api.url`     | `https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php` | Weather observations endpoint |

Any property can be overridden at startup, for example
`./gradlew bootRun --args='--weather.import.cron=0 */5 * * * *'`.

## Database

H2 database; the schema is created and seeded by Liquibase migrations in
`src/main/resources/db/changelog/changes`.

## Testing

> **Note:** Due to lack of time, I asked an AI assistant to create the unit tests for me.

```bash
./gradlew test
```

| Test class                         | Type                            | Covers                                                                                                                               |
|------------------------------------|---------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `DeliveryFeeServiceTest`           | Unit (Mockito)                  | All fee rules: the task example, all base fees, range boundaries, forbidden conditions, missing weather data, missing configuration |
| `WeatherObservationRepositoryTest` | Database (`@DataJpaTest`, H2)   | The newest observation of a station is returned; other stations are ignored; history is kept                                          |
| `DeliveryFeeControllerTest`        | Web layer (`@WebMvcTest`)       | JSON response and error mapping: 400, 422, 503, 500                                                                                  |
| `WeatherImportServiceTest`         | Unit (Mockito)                  | Only the three tracked stations are saved, with the import timestamp; nothing is saved when the API fails                            |
| `WeatherObservationMapperTest`     | Unit                            | Field mapping, Unix seconds to `Instant`, missing values stay `null`                                                                 |
| `ObservationsXmlParsingTest`       | Unit                            | The DTO annotations parse the weather API's XML format                                                                               |

Edge cases covered include air temperatures of -10.1, -10, 0 and 0.1 °C and wind speeds of 9.9,
10, 20 and 20.1 m/s.

### Test data

The tests do not depend on the weather website or on live weather:

- **Service and controller tests** use Mockito mocks. Each test creates its own weather
  observation, and the fee rules are copies of the seed data.
- **The repository test** uses a real in-memory H2 database created by the Liquibase migrations,
  filled with observations saved by the test itself.
- **The XML parsing test** reads a saved sample of the API response
  (`src/test/resources/observations-sample.xml`).

Fixed test data keeps results repeatable and makes it possible to test exact boundary values
on demand. The only exception is `DeliveryFeeApplicationTests`, which starts the whole
application; its startup import calls the real weather API.

## Design decisions

- **Fee rules are stored in the database** (Liquibase migrations seed them), not in code.
- **Range boundaries:** a rule with both a minimum and a maximum is inclusive on both ends
  ("between -10 °C and 0 °C"); a rule with only one bound is exclusive ("less than -10 °C").
- **Missing weather data:** if a value needed for the requested vehicle type is missing, the fee
  is not calculated and the API returns 503, rather than guessing a fee.
- **Forbidden conditions** (wind over 20 m/s; glaze, hail, thunder) are constants in
  `DeliveryFeeService`.
- **Weather history** is kept: every import inserts new rows and never overwrites old ones.

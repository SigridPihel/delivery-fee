package io.github.sigridpihel.deliveryfee.client.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that the XML annotations on the DTOs match the format of the weather API,
 * using a saved sample of the real response.
 */
class ObservationsXmlParsingTest {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void parsesTimestampAttributeAndAllStations() throws IOException {
        Observations observations = parseSample();

        assertThat(observations.timestamp()).isEqualTo(1792474102L);
        assertThat(observations.stations())
                .extracting(Station::stationName)
                .containsExactly("Tallinn-Harku", "Narva", "Tartu-Tõravere");
    }

    @Test
    void parsesStationFieldsAndIgnoresUnknownElements() throws IOException {
        Station tallinn = parseSample().stations().getFirst();

        assertThat(tallinn.wmoCode()).isEqualTo("26038");
        assertThat(tallinn.airTemperature()).isEqualByComparingTo("-5.3");
        assertThat(tallinn.windSpeed()).isEqualByComparingTo("3.1");
        assertThat(tallinn.phenomenon()).isEqualTo("Few clouds");
    }

    private Observations parseSample() throws IOException {
        try (InputStream xml = getClass().getResourceAsStream("/observations-sample.xml")) {
            return xmlMapper.readValue(xml, Observations.class);
        }
    }
}

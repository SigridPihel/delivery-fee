package io.github.sigridpihel.deliveryfee.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Observations(
        @JacksonXmlProperty(isAttribute = true, localName = "timestamp")
        Long timestamp,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "station")
        List<Station> stations
) {}

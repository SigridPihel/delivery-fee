package io.github.sigridpihel.deliveryfee.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Station (
        @JacksonXmlProperty(localName = "name")
        String stationName,

        @JacksonXmlProperty(localName = "wmocode")
        String wmoCode,

        @JacksonXmlProperty(localName = "airtemperature")
        BigDecimal airTemperature,

        @JacksonXmlProperty(localName = "windspeed")
        BigDecimal windSpeed,

        @JacksonXmlProperty(localName = "phenomenon")
        String phenomenon
){}

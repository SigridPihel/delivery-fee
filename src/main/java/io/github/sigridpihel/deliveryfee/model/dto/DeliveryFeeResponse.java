package io.github.sigridpihel.deliveryfee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record DeliveryFeeResponse(
        @Schema(description = "Total delivery fee in euros", example = "4.00")
        BigDecimal fee
) {}

package io.github.sigridpihel.deliveryfee.model.dto;

import java.math.BigDecimal;

public record DeliveryFeeResponse(
        BigDecimal fee
) {}

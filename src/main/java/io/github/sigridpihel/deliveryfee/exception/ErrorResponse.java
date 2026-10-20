package io.github.sigridpihel.deliveryfee.exception;

import java.util.Map;

public record ErrorResponse(String message, String timestamp, Map<String, String> fieldErrors) {
}

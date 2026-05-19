package com.telcoapihub.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

/**
 * CAMARA {@code sim-swap} payloads.
 */
public final class SimSwapDtos {

    private SimSwapDtos() {
    }

    public record CheckRequest(
            @NotBlank(message = "is required") String phoneNumber,
            @Min(value = 1, message = "must be at least 1 hour")
            @Max(value = 2400, message = "must be at most 2400 hours")
            Integer maxAge) {
    }

    public record CheckResponse(boolean swapped) {
    }

    public record DateRequest(
            @NotBlank(message = "is required") String phoneNumber) {
    }

    public record DateResponse(OffsetDateTime latestSimChange) {
    }
}

package com.telcoapihub.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * CAMARA {@code kyc-age-verification} payloads.
 */
public final class KycAgeDtos {

    private KycAgeDtos() {
    }

    public record AgeRequest(
            @NotNull(message = "is required")
            @Min(value = 13, message = "must be at least 13")
            @Max(value = 120, message = "must be at most 120")
            Integer ageThreshold,
            @NotBlank(message = "is required") String phoneNumber,
            String name,
            String givenName,
            String familyName,
            String birthdate,
            String email) {
    }

    /**
     * {@code ageCheck} is {@code "true"}, {@code "false"} or
     * {@code "not_available"}; {@code identityMatchScore} is 0-100.
     */
    public record AgeResponse(
            String ageCheck,
            Boolean verifiedStatus,
            Integer identityMatchScore) {
    }
}

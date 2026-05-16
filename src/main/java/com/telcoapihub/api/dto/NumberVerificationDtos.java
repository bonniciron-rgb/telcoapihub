package com.telcoapihub.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * CAMARA {@code number-verification} payloads.
 */
public final class NumberVerificationDtos {

    private NumberVerificationDtos() {
    }

    public record VerifyRequest(
            @NotBlank(message = "is required") String phoneNumber) {
    }

    public record VerifyResponse(boolean devicePhoneNumberVerified) {
    }

    public record DeviceNumberResponse(String devicePhoneNumber) {
    }
}

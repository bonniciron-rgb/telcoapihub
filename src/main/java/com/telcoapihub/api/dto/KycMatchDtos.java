package com.telcoapihub.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * CAMARA {@code kyc-match} payloads. The request field set here is a
 * representative subset; the full spec carries the complete identity
 * attribute list (name kana fields, street components, etc.).
 */
public final class KycMatchDtos {

    private KycMatchDtos() {
    }

    public record MatchRequest(
            @NotBlank(message = "is required") String phoneNumber,
            String idDocument,
            String name,
            String givenName,
            String familyName,
            String birthdate,
            String address,
            String postalCode,
            String country,
            String email) {
    }

    /**
     * Each field is {@code "true"}, {@code "false"} or {@code "not_available"}
     * per CAMARA. {@code null} means the attribute was not submitted.
     */
    public record MatchResponse(
            String idDocumentMatch,
            String nameMatch,
            String givenNameMatch,
            String familyNameMatch,
            String birthdateMatch,
            String addressMatch,
            String postalCodeMatch,
            String countryMatch,
            String emailMatch) {
    }
}

package com.telcoapihub.adapter;

import com.telcoapihub.api.dto.KycAgeDtos;
import com.telcoapihub.api.dto.KycMatchDtos;
import com.telcoapihub.api.dto.NumberVerificationDtos;
import com.telcoapihub.api.dto.SimSwapDtos;
import com.telcoapihub.domain.ApiProduct;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

/**
 * In-memory southbound supplier used until live operator/aggregator
 * integrations are connected. Results are deterministic functions of the
 * MSISDN so demos and tests are stable.
 */
@Component
public class MockSupplierAdapter implements SouthboundAdapter {

    public static final String CODE = "MOCK-AGG";

    @Override
    public String supplierCode() {
        return CODE;
    }

    @Override
    public boolean supports(ApiProduct product) {
        return true;
    }

    @Override
    public NumberVerificationDtos.VerifyResponse verifyNumber(String phoneNumber) {
        return new NumberVerificationDtos.VerifyResponse(bucket(phoneNumber, 10) != 0);
    }

    @Override
    public NumberVerificationDtos.DeviceNumberResponse devicePhoneNumber() {
        // In production this is derived from the 3-legged token's network context.
        return new NumberVerificationDtos.DeviceNumberResponse("+447700900123");
    }

    @Override
    public KycMatchDtos.MatchResponse matchKyc(KycMatchDtos.MatchRequest r) {
        return new KycMatchDtos.MatchResponse(
                match(r.idDocument(), 1),
                match(r.name(), 2),
                match(r.givenName(), 3),
                match(r.familyName(), 4),
                match(r.birthdate(), 5),
                match(r.address(), 6),
                match(r.postalCode(), 7),
                match(r.country(), 8),
                match(r.email(), 9));
    }

    @Override
    public KycAgeDtos.AgeResponse verifyAge(KycAgeDtos.AgeRequest r) {
        boolean overThreshold = bucket(r.phoneNumber(), 5) != 0;
        int score = 60 + bucket(r.phoneNumber(), 41);
        return new KycAgeDtos.AgeResponse(
                overThreshold ? "true" : "false",
                score >= 80,
                score);
    }

    @Override
    public SimSwapDtos.CheckResponse checkSimSwap(String phoneNumber, Integer maxAgeHours) {
        int hoursSinceSwap = bucket(phoneNumber, 2400);
        int window = (maxAgeHours == null) ? 240 : maxAgeHours;
        return new SimSwapDtos.CheckResponse(hoursSinceSwap <= window);
    }

    @Override
    public SimSwapDtos.DateResponse retrieveSimSwapDate(String phoneNumber) {
        int hoursSinceSwap = bucket(phoneNumber, 2400);
        return new SimSwapDtos.DateResponse(
                OffsetDateTime.now(ZoneOffset.UTC).minusHours(hoursSinceSwap));
    }

    /** Deterministic 0..(modulus-1) value derived from a string. */
    private static int bucket(String value, int modulus) {
        return Math.floorMod(String.valueOf(value).hashCode(), modulus);
    }

    /** CAMARA match result for one submitted attribute; null if not submitted. */
    private static String match(String value, int salt) {
        if (value == null || value.isBlank()) {
            return null;
        }
        int b = Math.floorMod(value.hashCode() + salt, 10);
        if (b == 0) {
            return "not_available";
        }
        return b <= 2 ? "false" : "true";
    }
}

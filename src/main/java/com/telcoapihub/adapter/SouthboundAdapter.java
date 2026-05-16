package com.telcoapihub.adapter;

import com.telcoapihub.api.dto.KycAgeDtos;
import com.telcoapihub.api.dto.KycMatchDtos;
import com.telcoapihub.api.dto.NumberVerificationDtos;
import com.telcoapihub.api.dto.SimSwapDtos;
import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.ApiProduct;

/**
 * Connector to one southbound supply source (an operator or aggregator).
 *
 * <p>Each implementation is responsible for translating a supplier's native
 * request/response format into the CAMARA-shaped DTOs returned here. An
 * adapter need only implement the operations its supplier offers; the
 * default methods report the rest as unavailable so the routing engine can
 * skip it.
 */
public interface SouthboundAdapter {

    /** Stable code linking this adapter to its {@code Supplier} record. */
    String supplierCode();

    boolean supports(ApiProduct product);

    default NumberVerificationDtos.VerifyResponse verifyNumber(String phoneNumber) {
        throw unsupported(ApiProduct.NUMBER_VERIFICATION);
    }

    default NumberVerificationDtos.DeviceNumberResponse devicePhoneNumber() {
        throw unsupported(ApiProduct.NUMBER_VERIFICATION);
    }

    default KycMatchDtos.MatchResponse matchKyc(KycMatchDtos.MatchRequest request) {
        throw unsupported(ApiProduct.KYC_MATCH);
    }

    default KycAgeDtos.AgeResponse verifyAge(KycAgeDtos.AgeRequest request) {
        throw unsupported(ApiProduct.KYC_AGE_VERIFICATION);
    }

    default SimSwapDtos.CheckResponse checkSimSwap(String phoneNumber, Integer maxAgeHours) {
        throw unsupported(ApiProduct.SIM_SWAP);
    }

    default SimSwapDtos.DateResponse retrieveSimSwapDate(String phoneNumber) {
        throw unsupported(ApiProduct.SIM_SWAP);
    }

    private static ApiException unsupported(ApiProduct product) {
        return ApiException.unavailable("Supplier does not support " + product);
    }
}

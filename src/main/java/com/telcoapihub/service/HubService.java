package com.telcoapihub.service;

import com.telcoapihub.adapter.SouthboundAdapter;
import com.telcoapihub.api.dto.KycAgeDtos;
import com.telcoapihub.api.dto.KycMatchDtos;
import com.telcoapihub.api.dto.NumberVerificationDtos;
import com.telcoapihub.api.dto.SimSwapDtos;
import com.telcoapihub.billing.MeteringService;
import com.telcoapihub.consent.ConsentService;
import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.Enterprise;
import com.telcoapihub.routing.RoutingDecision;
import com.telcoapihub.routing.RoutingEngine;
import com.telcoapihub.security.AuthContext;
import java.util.function.Function;
import org.springframework.stereotype.Service;

/**
 * Orchestrates every northbound API call: authenticate, enforce consent,
 * route to a supplier, invoke the adapter and meter the result.
 */
@Service
public class HubService {

    private final RoutingEngine routingEngine;
    private final ConsentService consentService;
    private final MeteringService meteringService;

    public HubService(RoutingEngine routingEngine,
                      ConsentService consentService,
                      MeteringService meteringService) {
        this.routingEngine = routingEngine;
        this.consentService = consentService;
        this.meteringService = meteringService;
    }

    public NumberVerificationDtos.VerifyResponse verifyNumber(NumberVerificationDtos.VerifyRequest request) {
        return execute(ApiProduct.NUMBER_VERIFICATION, "verify", request.phoneNumber(), false,
                adapter -> adapter.verifyNumber(request.phoneNumber()));
    }

    public NumberVerificationDtos.DeviceNumberResponse devicePhoneNumber() {
        return execute(ApiProduct.NUMBER_VERIFICATION, "device-phone-number", null, false,
                SouthboundAdapter::devicePhoneNumber);
    }

    public KycMatchDtos.MatchResponse matchKyc(KycMatchDtos.MatchRequest request) {
        return execute(ApiProduct.KYC_MATCH, "match", request.phoneNumber(), true,
                adapter -> adapter.matchKyc(request));
    }

    public KycAgeDtos.AgeResponse verifyAge(KycAgeDtos.AgeRequest request) {
        return execute(ApiProduct.KYC_AGE_VERIFICATION, "verify", request.phoneNumber(), true,
                adapter -> adapter.verifyAge(request));
    }

    public SimSwapDtos.CheckResponse checkSimSwap(SimSwapDtos.CheckRequest request) {
        return execute(ApiProduct.SIM_SWAP, "check", request.phoneNumber(), true,
                adapter -> adapter.checkSimSwap(request.phoneNumber(), request.maxAge()));
    }

    public SimSwapDtos.DateResponse retrieveSimSwapDate(SimSwapDtos.DateRequest request) {
        return execute(ApiProduct.SIM_SWAP, "retrieve-date", request.phoneNumber(), true,
                adapter -> adapter.retrieveSimSwapDate(request.phoneNumber()));
    }

    private <T> T execute(ApiProduct product, String operation, String msisdn,
                          boolean needsConsent, Function<SouthboundAdapter, T> call) {
        Enterprise enterprise = AuthContext.currentEnterprise();
        if (needsConsent) {
            consentService.requireConsent(enterprise.getId(), msisdn, product);
        }
        RoutingDecision routing = routingEngine.route(product, msisdn);

        long start = System.currentTimeMillis();
        try {
            T result = call.apply(routing.adapter());
            meteringService.record(enterprise.getId(), product, operation, msisdn, routing,
                    true, System.currentTimeMillis() - start);
            return result;
        } catch (RuntimeException ex) {
            meteringService.record(enterprise.getId(), product, operation, msisdn, routing,
                    false, System.currentTimeMillis() - start);
            throw ex;
        }
    }
}

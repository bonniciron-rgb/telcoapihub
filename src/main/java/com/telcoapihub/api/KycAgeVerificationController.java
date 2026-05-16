package com.telcoapihub.api;

import com.telcoapihub.api.dto.KycAgeDtos;
import com.telcoapihub.service.HubService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAMARA KYC Age Verification API.
 */
@RestController
@RequestMapping("/kyc-age-verification/v0")
public class KycAgeVerificationController {

    private final HubService hubService;

    public KycAgeVerificationController(HubService hubService) {
        this.hubService = hubService;
    }

    @PostMapping("/verify")
    public KycAgeDtos.AgeResponse verify(@Valid @RequestBody KycAgeDtos.AgeRequest request) {
        return hubService.verifyAge(request);
    }
}

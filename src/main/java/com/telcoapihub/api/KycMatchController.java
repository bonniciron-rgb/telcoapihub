package com.telcoapihub.api;

import com.telcoapihub.api.dto.KycMatchDtos;
import com.telcoapihub.service.HubService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAMARA KYC Match API.
 */
@RestController
@RequestMapping("/kyc-match/v0")
public class KycMatchController {

    private final HubService hubService;

    public KycMatchController(HubService hubService) {
        this.hubService = hubService;
    }

    @PostMapping("/match")
    public KycMatchDtos.MatchResponse match(@Valid @RequestBody KycMatchDtos.MatchRequest request) {
        return hubService.matchKyc(request);
    }
}

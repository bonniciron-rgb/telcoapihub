package com.telcoapihub.api;

import com.telcoapihub.api.dto.NumberVerificationDtos;
import com.telcoapihub.service.HubService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAMARA Number Verification API.
 */
@RestController
@RequestMapping("/number-verification/v0")
public class NumberVerificationController {

    private final HubService hubService;

    public NumberVerificationController(HubService hubService) {
        this.hubService = hubService;
    }

    @PostMapping("/verify")
    public NumberVerificationDtos.VerifyResponse verify(
            @Valid @RequestBody NumberVerificationDtos.VerifyRequest request) {
        return hubService.verifyNumber(request);
    }

    @GetMapping("/device-phone-number")
    public NumberVerificationDtos.DeviceNumberResponse devicePhoneNumber() {
        return hubService.devicePhoneNumber();
    }
}

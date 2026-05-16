package com.telcoapihub.api;

import com.telcoapihub.api.dto.SimSwapDtos;
import com.telcoapihub.service.HubService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAMARA SIM Swap API.
 */
@RestController
@RequestMapping("/sim-swap/v2")
public class SimSwapController {

    private final HubService hubService;

    public SimSwapController(HubService hubService) {
        this.hubService = hubService;
    }

    @PostMapping("/check")
    public SimSwapDtos.CheckResponse check(@Valid @RequestBody SimSwapDtos.CheckRequest request) {
        return hubService.checkSimSwap(request);
    }

    @PostMapping("/retrieve-date")
    public SimSwapDtos.DateResponse retrieveDate(@Valid @RequestBody SimSwapDtos.DateRequest request) {
        return hubService.retrieveSimSwapDate(request);
    }
}

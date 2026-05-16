package com.telcoapihub.consent;

import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.ConsentRecord;
import com.telcoapihub.security.AuthContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Enterprise-facing consent management. Stands in for the CAMARA ICM
 * consent capture flow.
 */
@RestController
@RequestMapping("/consent")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentRecord grant(@Valid @RequestBody GrantRequest request) {
        Long enterpriseId = AuthContext.currentEnterprise().getId();
        return consentService.grant(enterpriseId, request.msisdn(), request.product(), request.ttlDays());
    }

    @GetMapping
    public List<ConsentRecord> list() {
        return consentService.list(AuthContext.currentEnterprise().getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable Long id) {
        consentService.revoke(id, AuthContext.currentEnterprise().getId());
    }

    public record GrantRequest(
            @NotBlank(message = "is required") String msisdn,
            @NotNull(message = "is required") ApiProduct product,
            Integer ttlDays) {
    }
}

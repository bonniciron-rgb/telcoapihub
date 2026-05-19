package com.telcoapihub.consent;

import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.ConsentRecord;
import com.telcoapihub.domain.ConsentStatus;
import com.telcoapihub.repository.ConsentRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * End-user consent for personal-data products (KYC match, age verification,
 * SIM swap). Number verification is user-initiated and is not gated here.
 *
 * <p>In production, consent is captured through the CAMARA ICM OIDC/CIBA
 * flow; the direct grant API exists so the scaffold is exercisable.
 */
@Service
public class ConsentService {

    private final ConsentRepository consentRepository;

    public ConsentService(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    public boolean hasValidConsent(Long enterpriseId, String msisdn, ApiProduct product) {
        Instant now = Instant.now();
        return consentRepository
                .findByEnterpriseIdAndMsisdnAndProductAndStatus(
                        enterpriseId, msisdn, product, ConsentStatus.GRANTED)
                .stream()
                .anyMatch(c -> c.getExpiresAt() == null || c.getExpiresAt().isAfter(now));
    }

    public void requireConsent(Long enterpriseId, String msisdn, ApiProduct product) {
        if (!hasValidConsent(enterpriseId, msisdn, product)) {
            throw ApiException.permissionDenied(
                    "No valid end-user consent for " + product + " on the supplied number");
        }
    }

    public ConsentRecord grant(Long enterpriseId, String msisdn, ApiProduct product, Integer ttlDays) {
        ConsentRecord record = new ConsentRecord();
        record.setEnterpriseId(enterpriseId);
        record.setMsisdn(msisdn);
        record.setProduct(product);
        record.setStatus(ConsentStatus.GRANTED);
        record.setGrantedAt(Instant.now());
        if (ttlDays != null) {
            record.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        }
        return consentRepository.save(record);
    }

    public void revoke(Long consentId, Long enterpriseId) {
        ConsentRecord record = consentRepository.findById(consentId)
                .filter(c -> c.getEnterpriseId().equals(enterpriseId))
                .orElseThrow(() -> ApiException.notFound("Consent record not found"));
        record.setStatus(ConsentStatus.REVOKED);
        consentRepository.save(record);
    }

    public List<ConsentRecord> list(Long enterpriseId) {
        return consentRepository.findByEnterpriseId(enterpriseId);
    }
}

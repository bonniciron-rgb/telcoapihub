package com.telcoapihub.billing;

import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.UsageRecord;
import com.telcoapihub.repository.UsageRecordRepository;
import com.telcoapihub.routing.RoutingDecision;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Captures a CDR for every northbound API call. These records are the
 * billable events the rating and invoicing services later consume.
 */
@Service
public class MeteringService {

    private final UsageRecordRepository usageRecordRepository;

    public MeteringService(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
    }

    public UsageRecord record(Long enterpriseId, ApiProduct product, String operation,
                              String msisdn, RoutingDecision routing,
                              boolean success, long latencyMs) {
        UsageRecord usage = new UsageRecord();
        usage.setEnterpriseId(enterpriseId);
        usage.setProduct(product);
        usage.setOperation(operation);
        usage.setMsisdn(maskMsisdn(msisdn));
        usage.setSuccess(success);
        usage.setLatencyMs(latencyMs);
        usage.setOccurredAt(Instant.now());
        if (routing != null) {
            usage.setSupplierCode(routing.supplier().getCode());
            usage.setSupplierType(routing.supplier().getType());
            usage.setSupplierCost(success ? routing.supplier().getCostPerCall() : BigDecimal.ZERO);
        }
        return usageRecordRepository.save(usage);
    }

    /** Persist only the last four digits; full MSISDNs are PII. */
    private static String maskMsisdn(String msisdn) {
        if (msisdn == null || msisdn.length() < 4) {
            return "****";
        }
        return "****" + msisdn.substring(msisdn.length() - 4);
    }
}

package com.telcoapihub.config;

import com.telcoapihub.adapter.MockSupplierAdapter;
import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.ConsentRecord;
import com.telcoapihub.domain.ConsentStatus;
import com.telcoapihub.domain.Enterprise;
import com.telcoapihub.domain.PricingPlan;
import com.telcoapihub.domain.PricingTier;
import com.telcoapihub.domain.Supplier;
import com.telcoapihub.domain.SupplierType;
import com.telcoapihub.repository.ConsentRepository;
import com.telcoapihub.repository.EnterpriseRepository;
import com.telcoapihub.repository.PricingPlanRepository;
import com.telcoapihub.repository.SupplierRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a demo enterprise, pricing plan, supplier and consent so the hub
 * is exercisable on first boot. The in-memory database is recreated each
 * start, so this runs every time.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String DEMO_MSISDN = "+447700900123";

    private final PricingPlanRepository pricingPlanRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final SupplierRepository supplierRepository;
    private final ConsentRepository consentRepository;

    public DataSeeder(PricingPlanRepository pricingPlanRepository,
                      EnterpriseRepository enterpriseRepository,
                      SupplierRepository supplierRepository,
                      ConsentRepository consentRepository) {
        this.pricingPlanRepository = pricingPlanRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.supplierRepository = supplierRepository;
        this.consentRepository = consentRepository;
    }

    @Override
    public void run(String... args) {
        if (enterpriseRepository.count() > 0) {
            return;
        }

        PricingPlan plan = new PricingPlan();
        plan.setName("Standard");
        plan.setCurrency("EUR");
        plan.setMonthlyFee(new BigDecimal("99.00"));
        plan.getTiers().addAll(List.of(
                tier(ApiProduct.NUMBER_VERIFICATION, 0, 10_000L, "0.030"),
                tier(ApiProduct.NUMBER_VERIFICATION, 10_000, 100_000L, "0.020"),
                tier(ApiProduct.NUMBER_VERIFICATION, 100_000, null, "0.012"),
                tier(ApiProduct.KYC_MATCH, 0, 10_000L, "0.080"),
                tier(ApiProduct.KYC_MATCH, 10_000, null, "0.055"),
                tier(ApiProduct.KYC_AGE_VERIFICATION, 0, 10_000L, "0.060"),
                tier(ApiProduct.KYC_AGE_VERIFICATION, 10_000, null, "0.040"),
                tier(ApiProduct.SIM_SWAP, 0, 10_000L, "0.050"),
                tier(ApiProduct.SIM_SWAP, 10_000, null, "0.035")));
        plan = pricingPlanRepository.save(plan);

        Enterprise enterprise = new Enterprise();
        enterprise.setName("Acme Fintech");
        enterprise.setApiKey("demo-enterprise-key");
        enterprise.setPricingPlan(plan);
        enterprise = enterpriseRepository.save(enterprise);

        Supplier supplier = new Supplier();
        supplier.setName("Mock Aggregator");
        supplier.setCode(MockSupplierAdapter.CODE);
        supplier.setType(SupplierType.AGGREGATOR);
        supplier.setCostPerCall(new BigDecimal("0.010"));
        supplier.setEnabled(true);
        supplierRepository.save(supplier);

        for (ApiProduct product : List.of(
                ApiProduct.KYC_MATCH, ApiProduct.KYC_AGE_VERIFICATION, ApiProduct.SIM_SWAP)) {
            consentRepository.save(consent(enterprise.getId(), DEMO_MSISDN, product));
        }

        log.info("Seeded demo data: enterprise '{}' (X-API-Key: {}), consent on {}",
                enterprise.getName(), enterprise.getApiKey(), DEMO_MSISDN);
    }

    private static PricingTier tier(ApiProduct product, long from, Long to, String unitPrice) {
        return new PricingTier(product, from, to, new BigDecimal(unitPrice));
    }

    private static ConsentRecord consent(Long enterpriseId, String msisdn, ApiProduct product) {
        ConsentRecord record = new ConsentRecord();
        record.setEnterpriseId(enterpriseId);
        record.setMsisdn(msisdn);
        record.setProduct(product);
        record.setStatus(ConsentStatus.GRANTED);
        record.setGrantedAt(Instant.now());
        return record;
    }
}

package com.telcoapihub.billing;

import static org.assertj.core.api.Assertions.assertThat;

import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.PricingPlan;
import com.telcoapihub.domain.PricingTier;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class RatingServiceTest {

    private final RatingService ratingService = new RatingService();

    private PricingPlan planWithNumberVerificationTiers() {
        PricingPlan plan = new PricingPlan();
        plan.setTiers(List.of(
                new PricingTier(ApiProduct.NUMBER_VERIFICATION, 0, 10_000L, new BigDecimal("0.030")),
                new PricingTier(ApiProduct.NUMBER_VERIFICATION, 10_000, 100_000L, new BigDecimal("0.020")),
                new PricingTier(ApiProduct.NUMBER_VERIFICATION, 100_000, null, new BigDecimal("0.012"))));
        return plan;
    }

    @Test
    void ratesEntirelyWithinFirstTier() {
        BigDecimal amount = ratingService.rate(
                planWithNumberVerificationTiers(), ApiProduct.NUMBER_VERIFICATION, 5_000);

        assertThat(amount).isEqualByComparingTo("150.00");
    }

    @Test
    void ratesAcrossMultipleTiers() {
        BigDecimal amount = ratingService.rate(
                planWithNumberVerificationTiers(), ApiProduct.NUMBER_VERIFICATION, 50_000);

        // 10,000 @ 0.030 + 40,000 @ 0.020
        assertThat(amount).isEqualByComparingTo("1100.00");
    }

    @Test
    void ratesIntoOpenEndedTier() {
        BigDecimal amount = ratingService.rate(
                planWithNumberVerificationTiers(), ApiProduct.NUMBER_VERIFICATION, 150_000);

        // 10,000 @ 0.030 + 90,000 @ 0.020 + 50,000 @ 0.012
        assertThat(amount).isEqualByComparingTo("2700.00");
    }

    @Test
    void unpricedProductCostsNothing() {
        BigDecimal amount = ratingService.rate(
                planWithNumberVerificationTiers(), ApiProduct.SIM_SWAP, 1_000);

        assertThat(amount).isEqualByComparingTo("0.00");
    }
}

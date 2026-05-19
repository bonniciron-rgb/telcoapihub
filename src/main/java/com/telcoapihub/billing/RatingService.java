package com.telcoapihub.billing;

import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.PricingPlan;
import com.telcoapihub.domain.PricingTier;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Applies a plan's graduated volume tiers to a usage quantity.
 *
 * <p>Tiers are contiguous bands {@code [fromQuantity, toQuantity)}; the units
 * falling inside each band are priced at that band's unit price and summed.
 */
@Service
public class RatingService {

    public BigDecimal rate(PricingPlan plan, ApiProduct product, long quantity) {
        if (plan == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        List<PricingTier> tiers = plan.getTiers().stream()
                .filter(t -> t.getProduct() == product)
                .sorted(Comparator.comparingLong(PricingTier::getFromQuantity))
                .toList();

        BigDecimal total = BigDecimal.ZERO;
        for (PricingTier tier : tiers) {
            long upperBound = (tier.getToQuantity() == null)
                    ? quantity
                    : Math.min(quantity, tier.getToQuantity());
            long unitsInTier = Math.max(0, upperBound - tier.getFromQuantity());
            if (unitsInTier > 0) {
                total = total.add(tier.getUnitPrice().multiply(BigDecimal.valueOf(unitsInTier)));
            }
        }
        return total;
    }
}

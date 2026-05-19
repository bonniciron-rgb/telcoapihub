package com.telcoapihub.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

/**
 * One graduated volume tier for a product. {@code toQuantity} null means
 * the tier is open-ended (everything above {@code fromQuantity}).
 */
@Embeddable
public class PricingTier {

    @Enumerated(EnumType.STRING)
    private ApiProduct product;

    private long fromQuantity;

    private Long toQuantity;

    private BigDecimal unitPrice;

    public PricingTier() {
    }

    public PricingTier(ApiProduct product, long fromQuantity, Long toQuantity, BigDecimal unitPrice) {
        this.product = product;
        this.fromQuantity = fromQuantity;
        this.toQuantity = toQuantity;
        this.unitPrice = unitPrice;
    }

    public ApiProduct getProduct() { return product; }
    public void setProduct(ApiProduct product) { this.product = product; }

    public long getFromQuantity() { return fromQuantity; }
    public void setFromQuantity(long fromQuantity) { this.fromQuantity = fromQuantity; }

    public Long getToQuantity() { return toQuantity; }
    public void setToQuantity(Long toQuantity) { this.toQuantity = toQuantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}

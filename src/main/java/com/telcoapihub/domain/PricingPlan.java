package com.telcoapihub.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Northbound commercial plan: an optional recurring fee plus graduated
 * per-query volume tiers per product.
 */
@Entity
public class PricingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 3)
    private String currency = "EUR";

    @Column(nullable = false)
    private BigDecimal monthlyFee = BigDecimal.ZERO;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pricing_tier", joinColumns = @JoinColumn(name = "plan_id"))
    private List<PricingTier> tiers = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) { this.monthlyFee = monthlyFee; }

    public List<PricingTier> getTiers() { return tiers; }
    public void setTiers(List<PricingTier> tiers) { this.tiers = tiers; }
}

package com.telcoapihub.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/**
 * A southbound supply source. {@code code} links the commercial record
 * to its {@link com.telcoapihub.adapter.SouthboundAdapter} implementation.
 */
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierType type;

    /** ISO country the operator serves; null for multi-market aggregators. */
    private String countryCode;

    /** Per-query cost charged by this supplier (southbound cost of goods). */
    @Column(nullable = false)
    private BigDecimal costPerCall = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean enabled = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public SupplierType getType() { return type; }
    public void setType(SupplierType type) { this.type = type; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public BigDecimal getCostPerCall() { return costPerCall; }
    public void setCostPerCall(BigDecimal costPerCall) { this.costPerCall = costPerCall; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

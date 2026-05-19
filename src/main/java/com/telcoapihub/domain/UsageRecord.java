package com.telcoapihub.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * One metered API call (a CDR). Captured for every northbound request
 * regardless of outcome; rated into an {@link Invoice} at billing time.
 */
@Entity
@Table(name = "usage_record", indexes = {
        @Index(name = "idx_usage_enterprise", columnList = "enterpriseId,occurredAt")
})
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long enterpriseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiProduct product;

    @Column(nullable = false)
    private String operation;

    private String supplierCode;

    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;

    /** Subscriber identifier. Stored tokenized in production. */
    private String msisdn;

    @Column(nullable = false)
    private boolean success;

    private long latencyMs;

    /** Southbound cost of this call. */
    @Column(nullable = false)
    private BigDecimal supplierCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private Instant occurredAt = Instant.now();

    /** Set once the record has been rolled into an issued invoice. */
    private Long invoiceId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }

    public ApiProduct getProduct() { return product; }
    public void setProduct(ApiProduct product) { this.product = product; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public SupplierType getSupplierType() { return supplierType; }
    public void setSupplierType(SupplierType supplierType) { this.supplierType = supplierType; }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

    public BigDecimal getSupplierCost() { return supplierCost; }
    public void setSupplierCost(BigDecimal supplierCost) { this.supplierCost = supplierCost; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
}

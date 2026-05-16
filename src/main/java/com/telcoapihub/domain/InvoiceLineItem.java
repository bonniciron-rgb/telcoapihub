package com.telcoapihub.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public class InvoiceLineItem {

    private String description;

    @Enumerated(EnumType.STRING)
    private ApiProduct product;

    private long quantity;

    private BigDecimal amount;

    public InvoiceLineItem() {
    }

    public InvoiceLineItem(String description, ApiProduct product, long quantity, BigDecimal amount) {
        this.description = description;
        this.product = product;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ApiProduct getProduct() { return product; }
    public void setProduct(ApiProduct product) { this.product = product; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

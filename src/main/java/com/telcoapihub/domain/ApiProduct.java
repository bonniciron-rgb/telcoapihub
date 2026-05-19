package com.telcoapihub.domain;

/**
 * The CAMARA API products exposed northbound by the hub.
 */
public enum ApiProduct {
    NUMBER_VERIFICATION("Number Verification"),
    KYC_MATCH("KYC Match"),
    KYC_AGE_VERIFICATION("Age Verification"),
    SIM_SWAP("SIM Swap");

    private final String label;

    ApiProduct(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

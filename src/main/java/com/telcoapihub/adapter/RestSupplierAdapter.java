package com.telcoapihub.adapter;

import org.springframework.web.client.RestClient;

/**
 * Base class for live HTTP-based southbound suppliers.
 *
 * <p>A concrete operator or aggregator adapter extends this, registers as a
 * Spring {@code @Component}, declares the products it {@code supports(...)},
 * and overrides the matching {@link SouthboundAdapter} operations. Inside
 * each operation it calls the supplier with {@link #restClient} and maps the
 * supplier's native payload onto the CAMARA DTOs.
 *
 * <p>Supplier credentials, base URLs and per-product cost belong in
 * configuration / the {@code Supplier} record, not in code. This class is
 * intentionally not a component itself.
 */
public abstract class RestSupplierAdapter implements SouthboundAdapter {

    protected final RestClient restClient;

    protected RestSupplierAdapter(String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}

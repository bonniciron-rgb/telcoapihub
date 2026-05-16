package com.telcoapihub.routing;

import com.telcoapihub.adapter.SouthboundAdapter;
import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.Supplier;
import com.telcoapihub.domain.SupplierType;
import com.telcoapihub.repository.SupplierRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Selects a southbound supplier for each request.
 *
 * <p>Hybrid policy: prefer a direct operator integration that serves the
 * number's country, otherwise fall back to an aggregator, otherwise any
 * enabled supplier that supports the product.
 */
@Component
public class RoutingEngine {

    private final List<SouthboundAdapter> adapters;
    private final SupplierRepository supplierRepository;
    private final MsisdnResolver msisdnResolver;

    public RoutingEngine(List<SouthboundAdapter> adapters,
                         SupplierRepository supplierRepository,
                         MsisdnResolver msisdnResolver) {
        this.adapters = adapters;
        this.supplierRepository = supplierRepository;
        this.msisdnResolver = msisdnResolver;
    }

    public RoutingDecision route(ApiProduct product, String msisdn) {
        String country = msisdnResolver.resolveCountry(msisdn);

        List<RoutingDecision> candidates = new ArrayList<>();
        for (SouthboundAdapter adapter : adapters) {
            if (!adapter.supports(product)) {
                continue;
            }
            supplierRepository.findByCode(adapter.supplierCode())
                    .filter(Supplier::isEnabled)
                    .ifPresent(supplier -> candidates.add(new RoutingDecision(adapter, supplier)));
        }
        if (candidates.isEmpty()) {
            throw ApiException.unavailable("No supplier is currently available for " + product);
        }

        return candidates.stream()
                .filter(c -> c.supplier().getType() == SupplierType.OPERATOR
                        && country != null
                        && country.equals(c.supplier().getCountryCode()))
                .findFirst()
                .or(() -> candidates.stream()
                        .filter(c -> c.supplier().getType() == SupplierType.AGGREGATOR)
                        .findFirst())
                .orElse(candidates.get(0));
    }
}

package com.telcoapihub.routing;

import com.telcoapihub.adapter.SouthboundAdapter;
import com.telcoapihub.domain.Supplier;

/**
 * The supplier selected to serve one request, plus the adapter to call.
 */
public record RoutingDecision(SouthboundAdapter adapter, Supplier supplier) {
}

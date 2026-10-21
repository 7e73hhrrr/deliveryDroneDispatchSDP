package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import java.util.Objects;
public final class DistancePricing implements PricingStrategy {
    @Override public long quote(Delivery delivery) {
        Objects.requireNonNull(delivery);
        return 500L + 120L * delivery.distanceKm();
    }
}

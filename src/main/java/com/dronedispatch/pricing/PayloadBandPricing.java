package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import java.util.Objects;
public final class PayloadBandPricing implements PricingStrategy {
    @Override public long quote(Delivery delivery) {
        Objects.requireNonNull(delivery);
        return 700L + 250L * ((delivery.weightGrams() + 999) / 1000);
    }
}

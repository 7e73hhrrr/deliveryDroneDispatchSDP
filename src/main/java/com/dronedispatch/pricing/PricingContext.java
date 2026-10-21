package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import java.util.Objects;
/** Delegates only; never decides which algorithm belongs here. */
public final class PricingContext {
    private PricingStrategy strategy;
    public PricingContext(PricingStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(PricingStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
    public long quote(Delivery delivery) { return strategy.quote(delivery); }
}

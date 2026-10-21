package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.weather.WeatherSource;
/** Stable boundary retained so the original characterization tests need no edits. */
public final class PricingService {
    private final PricingSelector selector;
    public PricingService(WeatherSource weather) { this.selector = PricingSelector.standard(weather); }
    public long quote(String mode, Delivery delivery) {
        return new PricingContext(selector.manual(mode)).quote(delivery);
    }
}

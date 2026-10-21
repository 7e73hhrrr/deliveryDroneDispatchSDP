package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.weather.WeatherSource;
import java.util.Objects;
public final class WeatherEnergyPricing implements PricingStrategy {
    private final WeatherSource weather;
    public WeatherEnergyPricing(WeatherSource weather) { this.weather = Objects.requireNonNull(weather); }
    @Override public long quote(Delivery delivery) {
        Objects.requireNonNull(delivery);
        int wind = Objects.checkIndex(weather.windKmh(), 101);
        long units = (long) delivery.distanceKm() * ((delivery.weightGrams() + 999) / 1000);
        return 300L + units * (20L + wind);
    }
}

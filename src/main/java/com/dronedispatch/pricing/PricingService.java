package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.weather.WeatherSource;

import java.util.Objects;

public final class PricingService {

    private final WeatherSource weather;

    public PricingService(WeatherSource weather) {
        this.weather = Objects.requireNonNull(weather);
    }

    public long quote(String mode, Delivery delivery) {
        Objects.requireNonNull(delivery);

        return switch (mode) {
            case "distance" ->
                    500L + 120L * delivery.distanceKm();

            case "payload" ->
                    700L
                            + 250L
                            * ((delivery.weightGrams() + 999) / 1000);

            case "energy" -> {
                int wind = Objects.checkIndex(
                        weather.windKmh(),
                        101
                );

                long units = (long) delivery.distanceKm()
                        * ((delivery.weightGrams() + 999) / 1000);

                yield 300L + units * (20L + wind);
            }

            default -> throw new IllegalArgumentException(
                    "Unknown pricing mode: " + mode
            );
        };
    }
}
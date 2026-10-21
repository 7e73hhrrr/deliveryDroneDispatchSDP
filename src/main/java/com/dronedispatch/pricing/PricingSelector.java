package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.selection.SelectionRule;
import com.dronedispatch.weather.WeatherSource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PricingSelector {

    private final Map<String, PricingStrategy> strategies;
    private final List<SelectionRule<PricingStrategy>> rules;
    private final PricingStrategy fallback;

    public PricingSelector(
            Map<String, PricingStrategy> strategies,
            List<SelectionRule<PricingStrategy>> rules,
            PricingStrategy fallback
    ) {
        this.strategies = Map.copyOf(strategies);
        this.rules = List.copyOf(rules);
        this.fallback = Objects.requireNonNull(fallback);
    }

    public PricingStrategy manual(String key) {
        return Optional.ofNullable(
                strategies.get(Objects.requireNonNull(key))
        ).orElseThrow(
                () -> new IllegalArgumentException(
                        "Unknown strategy: " + key
                )
        );
    }

    public PricingStrategy automatic(Delivery delivery) {
        Objects.requireNonNull(delivery);

        return rules.stream()
                .filter(rule -> rule.condition().test(delivery))
                .findFirst()
                .map(SelectionRule::strategy)
                .orElse(fallback);
    }

    public static PricingSelector standard(WeatherSource weather) {
        Objects.requireNonNull(weather);

        PricingStrategy distance = new DistancePricing();
        PricingStrategy payload = new PayloadBandPricing();
        PricingStrategy energy = new WeatherEnergyPricing(weather);

        return new PricingSelector(
                Map.of(
                        "distance", distance,
                        "payload", payload,
                        "energy", energy
                ),
                List.of(
                        new SelectionRule<>(
                                delivery ->
                                        Objects.checkIndex(
                                                weather.windKmh(),
                                                101
                                        ) >= 30,
                                energy
                        ),
                        new SelectionRule<>(
                                delivery -> delivery.weightGrams() > 2000,
                                payload
                        )
                ),
                distance
        );
    }
}
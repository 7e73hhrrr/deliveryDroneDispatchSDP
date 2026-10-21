package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.selection.SelectionRule;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class DroneAssignmentSelector {

    private final Map<String, DroneAssignmentStrategy> strategies;
    private final List<SelectionRule<DroneAssignmentStrategy>> rules;
    private final DroneAssignmentStrategy fallback;

    public DroneAssignmentSelector(
            Map<String, DroneAssignmentStrategy> strategies,
            List<SelectionRule<DroneAssignmentStrategy>> rules,
            DroneAssignmentStrategy fallback
    ) {
        this.strategies = Map.copyOf(strategies);
        this.rules = List.copyOf(rules);
        this.fallback = Objects.requireNonNull(fallback);
    }

    public DroneAssignmentStrategy manual(String key) {
        return Optional.ofNullable(
                strategies.get(Objects.requireNonNull(key))
        ).orElseThrow(
                () -> new IllegalArgumentException(
                        "Unknown strategy: " + key
                )
        );
    }

    public DroneAssignmentStrategy automatic(Delivery delivery) {
        Objects.requireNonNull(delivery);

        return rules.stream()
                .filter(rule -> rule.condition().test(delivery))
                .findFirst()
                .map(SelectionRule::strategy)
                .orElse(fallback);
    }

    public static DroneAssignmentSelector standard() {
        DroneAssignmentStrategy nearest = new NearestDroneStrategy();
        DroneAssignmentStrategy bestFit = new BestFitPayloadStrategy();
        DroneAssignmentStrategy idle = new LongestIdleStrategy();

        return new DroneAssignmentSelector(
                Map.of(
                        "nearest", nearest,
                        "best-fit", bestFit,
                        "idle", idle
                ),
                List.of(
                        new SelectionRule<>(
                                Delivery::urgent,
                                nearest
                        ),
                        new SelectionRule<>(
                                delivery -> delivery.weightGrams() >= 3000,
                                bestFit
                        )
                ),
                idle
        );
    }
}
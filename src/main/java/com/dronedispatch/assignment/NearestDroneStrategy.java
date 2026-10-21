package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.model.Drone;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class NearestDroneStrategy
        implements DroneAssignmentStrategy {

    @Override
    public Optional<Drone> assign(
            Delivery delivery,
            List<Drone> fleet
    ) {
        return DroneEligibility.eligible(delivery, fleet)
                .min(
                        Comparator.comparingInt(Drone::pickupKm)
                                .thenComparing(Drone::id)
                );
    }
}
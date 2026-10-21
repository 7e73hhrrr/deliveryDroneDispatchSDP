package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.model.Drone;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class DroneEligibility {

    private DroneEligibility() {
    }

    public static Stream<Drone> eligible(
            Delivery delivery,
            List<Drone> fleet
    ) {
        Objects.requireNonNull(delivery);
        Objects.requireNonNull(fleet);

        return fleet.stream()
                .map(Objects::requireNonNull)
                .filter(Drone::available)
                .filter(
                        drone ->
                                drone.capacityGrams()
                                        >= delivery.weightGrams()
                )
                .filter(
                        drone ->
                                drone.rangeKm()
                                        >= drone.pickupKm()
                                        + 2 * delivery.distanceKm()
                );
    }
}
package com.dronedispatch.assignment;

import com.dronedispatch.model.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
/** Common feasibility constraint, independent of ranking policy. */
public final class DroneEligibility {
    private DroneEligibility() {}
    public static Stream<Drone> eligible(Delivery delivery, List<Drone> fleet) {
        Objects.requireNonNull(delivery);
        Objects.requireNonNull(fleet);
        return fleet.stream().map(Objects::requireNonNull)
            .filter(Drone::available)
            .filter(d -> d.capacityGrams() >= delivery.weightGrams())
            // Pickup + outward delivery + return to pickup point.
            .filter(d -> d.rangeKm() >= d.pickupKm() + 2 * delivery.distanceKm());
    }
}

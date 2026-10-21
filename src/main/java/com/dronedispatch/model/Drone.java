package com.dronedispatch.model;

import java.util.Objects;

public record Drone(
        String id,
        int capacityGrams,
        int pickupKm,
        int rangeKm,
        int idleMinutes,
        boolean available
) {
    public Drone {
        Objects.requireNonNull(id);

        Objects.checkIndex(id.length() - 1, 100);
        Objects.checkIndex(capacityGrams - 1, 20000);
        Objects.checkIndex(pickupKm, 1001);
        Objects.checkIndex(rangeKm, 5001);
        Objects.checkIndex(idleMinutes, 1000001);
    }
}
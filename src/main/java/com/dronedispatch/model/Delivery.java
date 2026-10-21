package com.dronedispatch.model;

import java.util.Objects;

public record Delivery(
        int distanceKm,
        int weightGrams,
        boolean urgent
) {
    public Delivery {
        Objects.checkIndex(distanceKm, 1001);
        Objects.checkIndex(weightGrams - 1, 20000);
    }
}
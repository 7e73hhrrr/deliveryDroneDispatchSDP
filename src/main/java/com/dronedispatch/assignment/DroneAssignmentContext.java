package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.model.Drone;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DroneAssignmentContext {

    private DroneAssignmentStrategy strategy;

    public DroneAssignmentContext(DroneAssignmentStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(DroneAssignmentStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public Optional<Drone> assign(
            Delivery delivery,
            List<Drone> fleet
    ) {
        return strategy.assign(delivery, fleet);
    }
}
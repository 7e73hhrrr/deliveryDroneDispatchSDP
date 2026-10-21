package com.dronedispatch.assignment;

import com.dronedispatch.model.*;
import java.util.*;
public final class DroneAssignmentContext {
    private DroneAssignmentStrategy strategy;
    public DroneAssignmentContext(DroneAssignmentStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(DroneAssignmentStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
    public Optional<Drone> assign(Delivery delivery, List<Drone> fleet) { return strategy.assign(delivery, fleet); }
}

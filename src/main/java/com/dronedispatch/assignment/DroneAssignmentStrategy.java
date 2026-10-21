package com.dronedispatch.assignment;

import com.dronedispatch.model.*;
import java.util.List;
import java.util.Optional;
@FunctionalInterface
public interface DroneAssignmentStrategy {
    Optional<Drone> assign(Delivery delivery, List<Drone> fleet);
}

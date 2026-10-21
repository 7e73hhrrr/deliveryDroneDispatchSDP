package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.model.Drone;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface DroneAssignmentStrategy {

    Optional<Drone> assign(Delivery delivery, List<Drone> fleet);
}
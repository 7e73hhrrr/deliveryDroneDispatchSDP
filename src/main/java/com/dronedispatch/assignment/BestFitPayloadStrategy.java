package com.dronedispatch.assignment;

import com.dronedispatch.model.*;
import java.util.*;
public final class BestFitPayloadStrategy implements DroneAssignmentStrategy {
    @Override public Optional<Drone> assign(Delivery delivery, List<Drone> fleet) {
        return DroneEligibility.eligible(delivery, fleet)
            .min((Comparator.comparingInt(Drone::capacityGrams)).thenComparing(Drone::id));
    }
}

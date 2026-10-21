package com.dronedispatch.assignment;

import com.dronedispatch.model.*;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
class DroneAssignmentStrategiesTest {
    private final Delivery delivery = new Delivery(10, 1500, false);
    private final List<Drone> fleet = List.of(
        new Drone("A", 5000, 1, 100, 10, true),
        new Drone("B", 2000, 4, 100, 20, true),
        new Drone("C", 4000, 3, 100, 90, true));
    static Stream<DroneAssignmentStrategy> strategies() {
        return Stream.of(new NearestDroneStrategy(), new BestFitPayloadStrategy(), new LongestIdleStrategy());
    }
    @Test void nearestMinimizesPickupDistance() { assertEquals("A", new NearestDroneStrategy().assign(delivery, fleet).orElseThrow().id()); }
    @Test void bestFitMinimizesUnusedCapacity() { assertEquals("B", new BestFitPayloadStrategy().assign(delivery, fleet).orElseThrow().id()); }
    @Test void longestIdleMaximizesWaitingTime() { assertEquals("C", new LongestIdleStrategy().assign(delivery, fleet).orElseThrow().id()); }
    @ParameterizedTest @MethodSource("strategies")
    void filtersUnavailableOverloadedAndOutOfRangeDrones(DroneAssignmentStrategy strategy) {
        Drone exact = new Drone("eligible", 1500, 2, 22, 0, true);
        List<Drone> candidates = List.of(
            new Drone("busy", 1500, 0, 100, 999, false),
            new Drone("small", 1499, 0, 100, 999, true),
            new Drone("short-range", 1500, 0, 19, 999, true), exact);
        assertEquals(exact, strategy.assign(delivery, candidates).orElseThrow());
        assertEquals(List.of("busy", "small", "short-range", "eligible"), candidates.stream().map(Drone::id).toList());
    }
    @ParameterizedTest @MethodSource("strategies")
    void emptyOrIneligibleFleetHasNoAssignment(DroneAssignmentStrategy strategy) {
        assertTrue(strategy.assign(delivery, List.of()).isEmpty());
        assertTrue(strategy.assign(delivery, List.of(new Drone("busy", 2000, 0, 100, 0, false))).isEmpty());
    }
    @ParameterizedTest @MethodSource("strategies")
    void tiesUseLexicalIdRegardlessOfInputOrder(DroneAssignmentStrategy strategy) {
        Drone a = new Drone("A", 2000, 1, 100, 10, true);
        Drone b = new Drone("B", 2000, 1, 100, 10, true);
        assertEquals(a, strategy.assign(delivery, List.of(b, a)).orElseThrow());
        assertEquals(a, strategy.assign(delivery, List.of(a, b)).orElseThrow());
    }
    @Test void contextSwitchesAtRuntime() {
        var context = new DroneAssignmentContext(new NearestDroneStrategy());
        assertEquals("A", context.assign(delivery, fleet).orElseThrow().id());
        context.setStrategy(new BestFitPayloadStrategy());
        assertEquals("B", context.assign(delivery, fleet).orElseThrow().id());
        context.setStrategy(new LongestIdleStrategy());
        assertEquals("C", context.assign(delivery, fleet).orElseThrow().id());
        assertThrows(NullPointerException.class, () -> context.setStrategy(null));
        assertThrows(NullPointerException.class, () -> new DroneAssignmentContext(null));
    }
}

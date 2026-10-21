package com.dronedispatch.model;

import com.dronedispatch.weather.SimulatedWeatherSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ModelValidationTest {
    @Test void deliveryRejectsInvalidBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Delivery(-1, 1, false));
        assertThrows(IndexOutOfBoundsException.class, () -> new Delivery(1001, 1, false));
        assertThrows(IndexOutOfBoundsException.class, () -> new Delivery(0, 0, false));
        assertThrows(IndexOutOfBoundsException.class, () -> new Delivery(0, 20001, false));
    }
    @Test void droneRejectsInvalidBounds() {
        assertThrows(NullPointerException.class, () -> new Drone(null, 1, 0, 0, 0, true));
        assertThrows(IndexOutOfBoundsException.class, () -> new Drone("", 1, 0, 0, 0, true));
        assertThrows(IndexOutOfBoundsException.class, () -> new Drone("A", 0, 0, 0, 0, true));
        assertThrows(IndexOutOfBoundsException.class, () -> new Drone("A", 1, -1, 0, 0, true));
        assertThrows(IndexOutOfBoundsException.class, () -> new Drone("A", 1, 0, -1, 0, true));
        assertThrows(IndexOutOfBoundsException.class, () -> new Drone("A", 1, 0, 0, -1, true));
    }
    @Test void simulatedSourceIsDeterministic() {
        assertEquals(30, new SimulatedWeatherSource(30).windKmh());
        assertThrows(IndexOutOfBoundsException.class, () -> new SimulatedWeatherSource(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> new SimulatedWeatherSource(101));
    }
}

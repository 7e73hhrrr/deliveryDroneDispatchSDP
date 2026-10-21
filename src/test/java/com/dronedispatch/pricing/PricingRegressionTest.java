package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.support.FakeWeatherSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
/** This exact file remains unchanged across the before/after refactoring commits. */
class PricingRegressionTest {
    private final PricingService service = new PricingService(new FakeWeatherSource(10));
    @ParameterizedTest
    @CsvSource({"distance, 10, 1500, 1700", "payload, 10, 1500, 1200", "energy, 10, 1500, 900",
        "distance, 0, 1, 500", "payload, 0, 1, 950", "energy, 0, 1, 300",
        "payload, 10, 1000, 950", "payload, 10, 1001, 1200",
        "distance, 1000, 20000, 120500", "payload, 1000, 20000, 5700", "energy, 1000, 20000, 600300"})
    void preservesExactQuotes(String mode, int km, int grams, long expected) {
        assertEquals(expected, service.quote(mode, new Delivery(km, grams, false)));
    }
    @Test void rejectsUnknownMode() {
        assertThrows(IllegalArgumentException.class, () -> service.quote("missing", new Delivery(1, 1, false)));
    }
    @Test void rejectsNullDelivery() { assertThrows(NullPointerException.class, () -> service.quote("distance", null)); }
    @Test void rejectsNullWeather() { assertThrows(NullPointerException.class, () -> new PricingService(null)); }
}

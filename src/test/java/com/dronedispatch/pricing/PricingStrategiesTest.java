package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.support.FakeWeatherSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
class PricingStrategiesTest {
    static Stream<PricingStrategy> strategies() {
        return Stream.of(new DistancePricing(), new PayloadBandPricing(), new WeatherEnergyPricing(new FakeWeatherSource(10)));
    }
    @Test void distanceChargesPerKilometreRegardlessOfPayload() {
        var strategy = new DistancePricing();
        assertEquals(1700, strategy.quote(new Delivery(10, 1500, false)));
        assertEquals(1820, strategy.quote(new Delivery(11, 1500, false)));
        assertEquals(1700, strategy.quote(new Delivery(10, 9000, true)));
    }
    @Test void payloadChargesStartedKilogramRegardlessOfDistance() {
        var strategy = new PayloadBandPricing();
        assertEquals(950, strategy.quote(new Delivery(10, 1000, false)));
        assertEquals(1200, strategy.quote(new Delivery(10, 1001, false)));
        assertEquals(1200, strategy.quote(new Delivery(99, 1001, true)));
    }
    @Test void energyCombinesDistancePayloadAndExternalWind() {
        Delivery d = new Delivery(10, 1500, false);
        assertEquals(700, new WeatherEnergyPricing(new FakeWeatherSource(0)).quote(d));
        assertEquals(900, new WeatherEnergyPricing(new FakeWeatherSource(10)).quote(d));
        assertEquals(1300, new WeatherEnergyPricing(new FakeWeatherSource(30)).quote(d));
        assertEquals(600, new WeatherEnergyPricing(new FakeWeatherSource(10)).quote(new Delivery(10, 1000, false)));
    }
    @Test void weatherIsReadAtExecutionTime() {
        var wind = new java.util.concurrent.atomic.AtomicInteger(0);
        var strategy = new WeatherEnergyPricing(wind::get);
        Delivery d = new Delivery(10, 1500, false);
        assertEquals(700, strategy.quote(d));
        wind.set(30);
        assertEquals(1300, strategy.quote(d));
    }
    @Test void rejectsInvalidExternalObservation() {
        assertThrows(IndexOutOfBoundsException.class, () -> new WeatherEnergyPricing(new FakeWeatherSource(-1)).quote(new Delivery(1, 1, false)));
        assertThrows(IndexOutOfBoundsException.class, () -> new WeatherEnergyPricing(new FakeWeatherSource(101)).quote(new Delivery(1, 1, false)));
        assertThrows(NullPointerException.class, () -> new WeatherEnergyPricing(null));
    }
    @ParameterizedTest @MethodSource("strategies")
    void pricesStayNonnegativeAcrossInputBoundaries(PricingStrategy strategy) {
        for (int km : new int[]{0, 1, 10, 1000}) {
            for (int grams : new int[]{1, 999, 1000, 1001, 20000}) {
                assertTrue(strategy.quote(new Delivery(km, grams, false)) >= 0);
            }
        }
    }
    @ParameterizedTest @MethodSource("strategies")
    void rejectsNullDelivery(PricingStrategy strategy) { assertThrows(NullPointerException.class, () -> strategy.quote(null)); }
}

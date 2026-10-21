package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.support.FakeWeatherSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategiesTest {

    static Stream<PricingStrategy> strategies() {
        return Stream.of(
                new DistancePricing(),
                new PayloadBandPricing(),
                new WeatherEnergyPricing(new FakeWeatherSource(10))
        );
    }

    @Test
    void distanceChargesPerKilometreRegardlessOfPayload() {
        var strategy = new DistancePricing();

        assertEquals(
                1700,
                strategy.quote(new Delivery(10, 1500, false))
        );

        assertEquals(
                1820,
                strategy.quote(new Delivery(11, 1500, false))
        );

        assertEquals(
                1700,
                strategy.quote(new Delivery(10, 9000, true))
        );
    }

    @Test
    void payloadChargesStartedKilogramRegardlessOfDistance() {
        var strategy = new PayloadBandPricing();

        assertEquals(
                950,
                strategy.quote(new Delivery(10, 1000, false))
        );

        assertEquals(
                1200,
                strategy.quote(new Delivery(10, 1001, false))
        );

        assertEquals(
                1200,
                strategy.quote(new Delivery(99, 1001, true))
        );
    }

    @Test
    void energyCombinesDistancePayloadAndExternalWind() {
        Delivery delivery = new Delivery(10, 1500, false);

        assertEquals(
                700,
                new WeatherEnergyPricing(
                        new FakeWeatherSource(0)
                ).quote(delivery)
        );

        assertEquals(
                900,
                new WeatherEnergyPricing(
                        new FakeWeatherSource(10)
                ).quote(delivery)
        );

        assertEquals(
                1300,
                new WeatherEnergyPricing(
                        new FakeWeatherSource(30)
                ).quote(delivery)
        );

        assertEquals(
                600,
                new WeatherEnergyPricing(
                        new FakeWeatherSource(10)
                ).quote(new Delivery(10, 1000, false))
        );
    }

    @Test
    void weatherIsReadAtExecutionTime() {
        var wind = new AtomicInteger(0);
        var strategy = new WeatherEnergyPricing(wind::get);

        Delivery delivery = new Delivery(10, 1500, false);

        assertEquals(700, strategy.quote(delivery));

        wind.set(30);

        assertEquals(1300, strategy.quote(delivery));
    }

    @Test
    void rejectsInvalidExternalObservation() {
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> new WeatherEnergyPricing(
                        new FakeWeatherSource(-1)
                ).quote(new Delivery(1, 1, false))
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> new WeatherEnergyPricing(
                        new FakeWeatherSource(101)
                ).quote(new Delivery(1, 1, false))
        );

        assertThrows(
                NullPointerException.class,
                () -> new WeatherEnergyPricing(null)
        );
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void pricesStayNonnegativeAcrossInputBoundaries(
            PricingStrategy strategy
    ) {
        for (int km : new int[]{0, 1, 10, 1000}) {
            for (int grams : new int[]{1, 999, 1000, 1001, 20000}) {
                assertTrue(
                        strategy.quote(
                                new Delivery(km, grams, false)
                        ) >= 0
                );
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void rejectsNullDelivery(PricingStrategy strategy) {
        assertThrows(
                NullPointerException.class,
                () -> strategy.quote(null)
        );
    }
}
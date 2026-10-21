package com.dronedispatch.app;

import com.dronedispatch.assignment.*;
import com.dronedispatch.model.*;
import com.dronedispatch.pricing.*;
import com.dronedispatch.weather.*;

import java.util.List;

public final class Demo {

    private Demo() {
    }

    public static void main(String[] args) {
        Delivery delivery = new Delivery(10, 1500, false);
        WeatherSource weather = new SimulatedWeatherSource(10);

        PricingSelector prices = PricingSelector.standard(weather);

        System.out.println(
                "SAME DELIVERY: " + delivery + "; wind = 10 km/h"
        );

        for (String key : List.of("distance", "payload", "energy")) {
            PricingContext context = new PricingContext(
                    prices.manual(key)
            );

            System.out.println(
                    key + " price = "
                            + context.quote(delivery)
                            + " minor units"
            );
        }

        PricingContext pricing = new PricingContext(
                prices.manual("payload")
        );

        System.out.println(
                "Manual payload = " + pricing.quote(delivery)
        );

        pricing.setStrategy(prices.automatic(delivery));

        System.out.println(
                "Same context, automatic = " + pricing.quote(delivery)
        );

        pricing.setStrategy(prices.manual("energy"));

        System.out.println(
                "Same context, switched to energy = "
                        + pricing.quote(delivery)
        );

        PricingSelector windySelector = PricingSelector.standard(
                new SimulatedWeatherSource(30)
        );

        PricingContext windyPricing = new PricingContext(
                windySelector.automatic(delivery)
        );

        System.out.println(
                "Automatic strong-wind price = "
                        + windyPricing.quote(delivery)
        );

        List<Drone> fleet = List.of(
                new Drone("A", 5000, 1, 100, 10, true),
                new Drone("B", 2000, 4, 100, 20, true),
                new Drone("C", 4000, 3, 100, 90, true)
        );

        DroneAssignmentSelector drones =
                DroneAssignmentSelector.standard();

        System.out.println("SAME FLEET: " + fleet);

        for (String key : List.of("nearest", "best-fit", "idle")) {
            DroneAssignmentContext context =
                    new DroneAssignmentContext(drones.manual(key));

            String selectedId = context.assign(delivery, fleet)
                    .map(Drone::id)
                    .orElse("No eligible drone");

            System.out.println(key + " selects " + selectedId);
        }

        DroneAssignmentContext dispatch = new DroneAssignmentContext(
                drones.manual("nearest")
        );

        System.out.println(
                "Manual nearest = "
                        + dispatch.assign(delivery, fleet)
                        .orElseThrow()
                        .id()
        );

        dispatch.setStrategy(drones.automatic(delivery));

        System.out.println(
                "Same context, automatic = "
                        + dispatch.assign(delivery, fleet)
                        .orElseThrow()
                        .id()
        );

        dispatch.setStrategy(drones.manual("best-fit"));

        System.out.println(
                "Same context, switched to best-fit = "
                        + dispatch.assign(delivery, fleet)
                        .orElseThrow()
                        .id()
        );

        System.out.println(
                "Empty fleet = " + dispatch.assign(delivery, List.of())
        );
    }
}
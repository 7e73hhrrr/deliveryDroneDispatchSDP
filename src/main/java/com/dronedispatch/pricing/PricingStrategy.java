package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;

@FunctionalInterface
public interface PricingStrategy {

    long quote(Delivery delivery);
}
package com.dronedispatch.selection;

import com.dronedispatch.model.Delivery;

import java.util.Objects;
import java.util.function.Predicate;

public record SelectionRule<S>(
        Predicate<Delivery> condition,
        S strategy
) {
    public SelectionRule {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(strategy);
    }
}
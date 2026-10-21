package com.dronedispatch.assignment;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.selection.SelectionRule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DroneAssignmentSelectorTest {

    @ParameterizedTest
    @CsvSource({
            "false, 2999, idle",
            "false, 3000, best-fit",
            "true, 2999, nearest",
            "true, 3000, nearest",
            "true, 20000, nearest"
    })
    void automaticRulesIncludeBoundariesAndPriority(
            boolean urgent,
            int grams,
            String expected
    ) {
        var selector = DroneAssignmentSelector.standard();

        assertSame(
                selector.manual(expected),
                selector.automatic(
                        new Delivery(10, grams, urgent)
                )
        );
    }

    @Test
    void manualChoicesAndUnknownKeys() {
        var selector = DroneAssignmentSelector.standard();

        assertInstanceOf(
                NearestDroneStrategy.class,
                selector.manual("nearest")
        );

        assertInstanceOf(
                BestFitPayloadStrategy.class,
                selector.manual("best-fit")
        );

        assertInstanceOf(
                LongestIdleStrategy.class,
                selector.manual("idle")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> selector.manual("unknown")
        );
    }

    @Test
    void newPolicyCanBeRegisteredWithoutEditingExistingClasses() {
        DroneAssignmentStrategy custom =
                (delivery, fleet) -> Optional.empty();

        var fallback = new LongestIdleStrategy();

        var selector = new DroneAssignmentSelector(
                Map.of("custom", custom),
                List.of(
                        new SelectionRule<>(
                                Delivery::urgent,
                                custom
                        )
                ),
                fallback
        );

        assertSame(custom, selector.manual("custom"));

        assertSame(
                custom,
                selector.automatic(new Delivery(1, 1, true))
        );

        assertSame(
                fallback,
                selector.automatic(new Delivery(1, 1, false))
        );
    }
}
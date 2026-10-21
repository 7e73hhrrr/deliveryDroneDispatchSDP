package com.dronedispatch.pricing;

import com.dronedispatch.model.Delivery;
import com.dronedispatch.selection.SelectionRule;
import com.dronedispatch.support.FakeWeatherSource;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
class PricingSelectorTest {
    @ParameterizedTest
    @CsvSource({"29,2000,distance", "29,2001,payload", "30,2000,energy", "30,2001,energy", "0,1,distance", "100,20000,energy"})
    void automaticRulesIncludeBoundariesAndPriority(int wind, int grams, String expectedKey) {
        var selector = PricingSelector.standard(new FakeWeatherSource(wind));
        assertSame(selector.manual(expectedKey), selector.automatic(new Delivery(10, grams, false)));
    }
    @Test void manualCanOverrideAutomaticSelection() {
        var selector = PricingSelector.standard(new FakeWeatherSource(30));
        assertInstanceOf(DistancePricing.class, selector.manual("distance"));
        assertInstanceOf(PayloadBandPricing.class, selector.manual("payload"));
        assertInstanceOf(WeatherEnergyPricing.class, selector.manual("energy"));
        assertNotSame(selector.manual("distance"), selector.automatic(new Delivery(10, 1, false)));
        assertThrows(IllegalArgumentException.class, () -> selector.manual("unknown"));
    }
    @Test void extendsManualAndAutomaticPoliciesWithoutEditingSelector() {
        PricingStrategy newAlgorithm = d -> 42L;
        PricingStrategy fallback = new DistancePricing();
        var selector = new PricingSelector(Map.of("new", newAlgorithm),
            List.of(new SelectionRule<>(Delivery::urgent, newAlgorithm)), fallback);
        assertSame(newAlgorithm, selector.manual("new"));
        assertSame(newAlgorithm, selector.automatic(new Delivery(1, 1, true)));
        assertSame(fallback, selector.automatic(new Delivery(1, 1, false)));
    }
    @Test void contextsDelegateAndSwitchOnTheSameObject() {
        Delivery d = new Delivery(10, 1500, false);
        PricingContext context = new PricingContext(new DistancePricing());
        assertEquals(1700, context.quote(d));
        context.setStrategy(new PayloadBandPricing());
        assertEquals(1200, context.quote(d));
        context.setStrategy(new WeatherEnergyPricing(new FakeWeatherSource(10)));
        assertEquals(900, context.quote(d));
        assertThrows(NullPointerException.class, () -> context.setStrategy(null));
        assertThrows(NullPointerException.class, () -> new PricingContext(null));
    }
}

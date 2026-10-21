package com.dronedispatch.weather;

import java.util.Objects;
/** Replace this adapter with a real provider without changing pricing algorithms. */
public final class SimulatedWeatherSource implements WeatherSource {
    private final int windKmh;
    public SimulatedWeatherSource(int windKmh) {
        this.windKmh = Objects.checkIndex(windKmh, 101);
    }
    @Override public int windKmh() { return windKmh; }
}

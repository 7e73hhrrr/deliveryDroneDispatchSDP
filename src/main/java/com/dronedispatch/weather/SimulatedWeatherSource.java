package com.dronedispatch.weather;

import java.util.Objects;

public final class SimulatedWeatherSource implements WeatherSource {

    private final int windKmh;

    public SimulatedWeatherSource(int windKmh) {
        this.windKmh = Objects.checkIndex(windKmh, 101);
    }

    @Override
    public int windKmh() {
        return windKmh;
    }
}
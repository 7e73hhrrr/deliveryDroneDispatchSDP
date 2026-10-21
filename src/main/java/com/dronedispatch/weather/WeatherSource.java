package com.dronedispatch.weather;

/** External-data boundary: implementations supply a wind observation in km/h. */
@FunctionalInterface
public interface WeatherSource {
    int windKmh();
}

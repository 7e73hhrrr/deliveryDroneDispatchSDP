package com.dronedispatch.weather;

@FunctionalInterface
public interface WeatherSource {

    int windKmh();
}
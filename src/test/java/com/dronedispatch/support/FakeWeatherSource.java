package com.dronedispatch.support;

import com.dronedispatch.weather.WeatherSource;
/** Fixed fake makes tests independent of time, network, or actual weather. */
public record FakeWeatherSource(int windKmh) implements WeatherSource {}

package com.dronedispatch.support;

import com.dronedispatch.weather.WeatherSource;

public record FakeWeatherSource(int windKmh) implements WeatherSource {
}
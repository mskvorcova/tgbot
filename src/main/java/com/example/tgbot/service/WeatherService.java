package com.example.tgbot.service;

public class WeatherService {
    private static String city;
    public WeatherService(String city) {
        WeatherService.city = city;
    }
    public String[] parseJSON() {
        String[] res = {"a", "b", city};
        return res;
    }
}

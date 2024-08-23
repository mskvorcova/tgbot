package com.example.tgbot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tgbot.model.City;
import com.example.tgbot.model.CityRepository;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public void addCity(String cityName) {
        Optional<City> city = cityRepository.findByCityName(cityName);
        if (city.isEmpty()) {
            City newCity = new City(); 
            newCity.setCityName(cityName);
            cityRepository.save(newCity);
        }
    }
}

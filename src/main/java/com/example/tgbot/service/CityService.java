package com.example.tgbot.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tgbot.model.City;
import com.example.tgbot.model.CityRepository;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public void addCity(String cityName) throws UnsupportedEncodingException {
        cityName = URLDecoder.decode(cityName, StandardCharsets.UTF_8.toString());
        Optional<City> city = cityRepository.findByCityName(cityName);
        if (city.isEmpty()) {
            City newCity = new City(); 
            newCity.setCityName(cityName);
            cityRepository.save(newCity);
        }
    }

    public boolean cityExists(String cityName) throws UnsupportedEncodingException {
        cityName = URLDecoder.decode(cityName, StandardCharsets.UTF_8.toString());
        Optional<City> city = cityRepository.findByCityName(cityName);
        return city.isPresent(); 
    }
}

package com.example.tgbot.model;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long>{
    Optional<City> findByCityName(String cityName);
}

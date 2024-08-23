package com.example.tgbot.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    public String getWeather(String place) throws IOException, ParseException {
        place = URLEncoder.encode(place, StandardCharsets.UTF_8.toString());
        String urlString = "https://api.openweathermap.org/data/2.5/weather?lang=ru&q=" + 
                            place + "&units=metric&appid=fdb5fb7907d39ba695179b6e052165a6";
        String jsonString = new JsonInfo().getInfo(place, urlString);
        JsonNode weatherArray = new ObjectMapper().readTree(jsonString).get("weather");
        StringBuilder sb = new StringBuilder();
        if (weatherArray.isArray()) {
            JsonNode weatherNode = weatherArray.get(0);
            String description = weatherNode.get("description").asText();
            sb.append("погода в городе ").append(place).append(": ").append(description).append("\n");
        }   
        JsonNode main = new ObjectMapper().readTree(jsonString).get("main");
        sb.append("температура: ").append(main.get("temp").asText()).append("\n");
        sb.append("ощущается как: ").append(main.get("feels_like").asText()).append("\n");
        sb.append("влажность: ").append(main.get("humidity").asText());     
        return sb.toString();
    }
}

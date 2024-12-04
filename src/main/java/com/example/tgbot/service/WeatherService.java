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
                            place + "&units=metric&appid=";
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

    public String getForecast(String place) throws IOException, ParseException {
        place = URLEncoder.encode(place, StandardCharsets.UTF_8.toString());
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lang=ru&q=" + 
                            place + "&units=metric&appid=";
        String jsonString = new JsonInfo().getInfo(place, urlString);
        JsonNode weatherArray = new ObjectMapper().readTree(jsonString).get("list");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if (weatherArray.isArray()) {
            for (JsonNode forecastNode : weatherArray) {
                if (count >= 4) break; 

                String dateTime = forecastNode.get("dt_txt").asText();
                JsonNode weatherNode = forecastNode.get("weather").get(0);
                String description = weatherNode.get("description").asText();
                JsonNode mainNode = forecastNode.get("main");

                sb.append("Прогноз на ").append(dateTime).append(":\n");
                sb.append("Погода: ").append(description).append("\n");
                sb.append("Температура: ").append(mainNode.get("temp").asText()).append("\n");
                sb.append("Ощущается как: ").append(mainNode.get("feels_like").asText()).append("\n");
                sb.append("Влажность: ").append(mainNode.get("humidity").asText()).append("\n\n");

                count++;
            }
        }
        
        return sb.toString();
    }
}

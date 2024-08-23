package com.example.tgbot.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ValidatorService {
    @Autowired
    CityService cityService;
    public boolean mainCheck(String checkPlace) throws ParseException, IOException {
        checkPlace = URLEncoder.encode(checkPlace, StandardCharsets.UTF_8.toString());
        String urlString = "https://nominatim.openstreetmap.org/search?q=" + checkPlace + "&format=json";
        String jsonString = new JsonInfo().getInfo(checkPlace, urlString);
        JsonNode arrNode = new ObjectMapper().readTree(jsonString);
        if (arrNode.isArray()) {
            for (final JsonNode node : arrNode) {
                String type = node.get("addresstype").toString();
                if ("\"city\"".equals(type)) {
                    cityService.addCity(checkPlace);
                    return true;
                }
            }
        }
        return false;
    }
}

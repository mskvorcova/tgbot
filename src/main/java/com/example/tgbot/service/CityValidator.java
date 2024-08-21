package com.example.tgbot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CityValidator {

    private final String[] checkPlace;
    public CityValidator(String[] checkPlace) {
        this.checkPlace = checkPlace;
    }
    public boolean check() throws ParseException, IOException {
        String jsonString = getInfo();
        JsonNode arrNode = new ObjectMapper().readTree(jsonString);
        if (arrNode.isArray()) {
            for (final JsonNode node : arrNode) {
                String type = node.get("addresstype").toString();
                if ("\"city\"".equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getInfo() throws IOException, ParseException {
        checkPlace[0] = URLEncoder.encode(checkPlace[0], StandardCharsets.UTF_8.toString());
        checkPlace[1] = URLEncoder.encode(checkPlace[1], StandardCharsets.UTF_8.toString());
        String urlString = "https://nominatim.openstreetmap.org/search?q=" +
                            checkPlace[0] + "+" + checkPlace[1] + 
                            "&format=json";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) url.getContent(), StandardCharsets.UTF_8))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }
}

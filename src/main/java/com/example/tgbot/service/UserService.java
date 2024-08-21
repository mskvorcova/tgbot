package com.example.tgbot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tgbot.model.User;
import com.example.tgbot.model.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUserCity(Long chatId, String city, String country) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setId(chatId);
        user.setCity(city);
        user.setCountry(country);
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long chatId) {
        return userRepository.findById(chatId);
    }
}


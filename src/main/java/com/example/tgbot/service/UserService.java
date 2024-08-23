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

    public void createUser(long chatId) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setId(chatId);
        userRepository.save(user);
    }

    public void setCity(long chatId, String city) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setId(chatId);
        user.setCity(city);
        userRepository.save(user);
    }

    public String getCity(long chatId) {
        Optional<User> existingUser = userRepository.findById(chatId);
        String res = "";
        if (existingUser.isPresent()) {
            User usr = existingUser.get();
            res = usr.getCity();
        }
        return res;
    }
}


package com.example.tgbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tgbot")
@Getter
@Setter
public class User {
    @Id
    private long id;
    private String city;
    private String country;
}


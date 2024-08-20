package com.example.tgbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component 
public class TelegramBot extends TelegramLongPollingBot {

    final String botName;
    final String botToken;
    private status state = status.WAIT_FOR_COMMAND;
    private String city;

    public TelegramBot(@Value("${bot.name}") String botName, 
    @Value("${bot.token}") String botToken) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (state) {
                case WAIT_FOR_COMMAND -> handleCommand(message, chatId, update);
                case WAIT_FOR_CITY -> changeCity(message, chatId);
                default -> sendMessage(chatId, "unknown command");
            }
        }
    }

    private void handleCommand(String cmd, long chatId, Update update) {
        switch (cmd) {
            case "/start" -> startCommand(chatId, update);
            case "/change" -> changeCommand(chatId);
            case "/currentCity" -> getCommand(chatId);
            case "getWeather" -> weatherCommand(chatId);
            default -> sendMessage(chatId, "Unknown command");
        }
    }

    private void changeCity(String newCity, long chatId) {
        city = newCity;
        String msg = "город установлен: " + newCity;
        sendMessage(chatId, msg);
        state = status.WAIT_FOR_COMMAND;
    }

    private void weatherCommand(long chatId) {
        WeatherService weather = new WeatherService(city);
        String[] data = weather.parseJSON();
    }

    private void startCommand(long chatId, Update update) {
        String ans = "привет, " + update.getMessage().getChat().getFirstName() + "! это бот помощник," + 
                    "для начала установи город в котором находишься :)";

        sendMessage(chatId, ans);
        state = status.WAIT_FOR_CITY;
    }

    private void changeCommand(long chatId) {
        String ans = "отправь в следующем сообщении город, для которого хочешь получать прогнозы";
        sendMessage(chatId, ans);
        state = status.WAIT_FOR_CITY;
    }

    private void getCommand(long chatId) {
        String ans;
        if (!city.isEmpty()) {
            ans = "выбранный город: " + city;
        }
        else {
            ans = "город не выбран";
        }
        sendMessage(chatId, ans);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }

    private enum status {
        WAIT_FOR_CITY, WAIT_FOR_COMMAND;
    }
}

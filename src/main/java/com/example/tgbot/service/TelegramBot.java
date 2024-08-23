package com.example.tgbot.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

@Component 
public class TelegramBot extends TelegramLongPollingBot {

    final String botName;
    final String botToken;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private WeatherService weatherService;
    private status state = status.WAIT_FOR_COMMAND;
    public TelegramBot(@Value("${bot.name}") String botName, 
    @Value("${bot.token}") String botToken) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "начать"));
        commands.add(new BotCommand("/setcity", "поменять основной город"));
        commands.add(new BotCommand("/currentcity", "установленный город"));
        commands.add(new BotCommand("/getweather", "погода в текущем городе"));
        commands.add(new BotCommand("/getforecast", "прогноз для текущего города"));
        try {
            SetMyCommands setMyCommands = new SetMyCommands();
            setMyCommands.setCommands(commands);
            setMyCommands.setScope(new BotCommandScopeDefault());
            setMyCommands.setLanguageCode(null); 
            execute(setMyCommands);
        } catch (TelegramApiException e) {
        }
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (state) {
                case WAIT_FOR_COMMAND -> {
                    try {
                        handleCommand(message, chatId, update);
                    } catch (IOException | ParseException e) {
                    }
                }
                case WAIT_FOR_CITY -> {
                    try {
                        changeCity(message, chatId);
                    } catch (ParseException | IOException ex) {
                    }
                }
                default -> sendMessage(chatId, "unknown command");
            }
        }
    }

    private void handleCommand(String cmd, long chatId, Update update) throws IOException, ParseException {
        switch (cmd.split(" ")[0]) {
            case "/start" -> startCommand(chatId, update);
            case "/setcity" -> changeCommand(chatId);
            case "/currentcity" -> getCommand(chatId);
            case "/getweather" -> handleWeather(chatId, update, true);
            case "/getforecast" -> handleWeather(chatId, update, false);
            default -> sendMessage(chatId, "Unknown command");
        }
    }

    private void changeCity(String newPlace, long chatId) throws ParseException, IOException {
        newPlace = newPlace.trim();
        if (newPlace.isEmpty()) {
            sendMessage(chatId, "Ошибка: Пожалуйста, введите корректные город и страну.");
            return;
        }
        if (checkPlace(newPlace, chatId)) {
            userService.setCity(chatId, newPlace);
            state = status.WAIT_FOR_COMMAND;
            sendMessage(chatId, "ура город установлен");
        }
    }

    private boolean checkPlace(String place, long chatId) throws ParseException, IOException {
        boolean checked = validatorService.mainCheck(place);
        if (checked) {
            return checked;
        }
        else {
            sendMessage(chatId, "такого города не существует, попробуйте еще раз");
            return checked;
        }
    }

    private void handleWeather(long chatId, Update update, boolean flag) throws IOException, ParseException {
        String place;
        String[] cmd = update.getMessage().getText().trim().split(" ");
        if (cmd.length == 1) {
            place = userService.getCity(chatId);
        } else {
            if (checkPlace(cmd[1], chatId)) {
                place = cmd[1];
            }
            else {
                return;
            }
        }
        String msg;
        if (flag) {
            msg = weatherService.getWeather(place);
        }
        else {
            
            msg = weatherService.getForecast(place);
            System.out.println(msg);
        } 
        sendMessage(chatId, msg);
    }

    private void startCommand(long chatId, Update update) {
        String ans = EmojiParser.parseToUnicode(":slightly_smiling_face:");
        userService.createUser(chatId);
        sendMessage(chatId, ans);
        state = status.WAIT_FOR_CITY;
    }

    private void changeCommand(long chatId) {
        String ans = "отправь в следующем сообщении город, для которого хочешь получать прогнозы";
        sendMessage(chatId, ans);
        state = status.WAIT_FOR_CITY;
    }

    private void getCommand(long chatId) {
        String place = userService.getCity(chatId);
        String ans;
        if (place.length() != 0) {
            ans = "выбранный город: " + place;
        }
        else {
            ans = "город не установлен";
        }
        sendMessage(chatId, ans);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        String decoded = URLDecoder.decode(text, StandardCharsets.UTF_8);
        msg.setText(decoded);
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

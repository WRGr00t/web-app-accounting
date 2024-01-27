package com.example.webappaccounting.service;

import com.example.webappaccounting.config.BotConfig;
import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {


    final BotConfig config;

    public TelegramBotService(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message updateMessage = update.getMessage();
        if (update.hasMessage() && updateMessage.hasText()){

            long chatId = updateMessage.getChatId();
            String message = updateMessage.getText();

            switch (message) {
                case "/start": startCommandReceived(
                        chatId,
                        updateMessage.getChat().getFirstName()
                );
                    break;

                case "/today": todayCommandReceived(chatId);
                    break;

                case "/tomorrow": tomorrowCommandReceived(chatId);
                    break;

                default:
                    sendMessage(chatId, "Не понял вопрос, повтори понятнее");
            }
            
        }
    }

    private void tomorrowCommandReceived(long chatId) {
        sendMessage(chatId, findAllByDate(LocalDate.now().plusDays(1)));
    }

    private void todayCommandReceived(long chatId) {


        sendMessage(chatId, findAllByDate());
    }

    private void addList(StringBuilder builder, ArrayList<Shift> list) {
        for (Shift shift : list) {
            builder.append(shift.getName())
                    .append("\n");
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет, " + name + ", как дела?\n Введите команду /today для выда списка смены на сегодня \n" +
                "или команду /tomorrow для завтрашней смены";
        log.info("User " + name + " start chat");
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException telegramApiException) {
            log.error(telegramApiException.getMessage());
        }
    }

    public String findAllByDate() {
        final String uri = "http://localhost:8080/api";

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }

    public String findAllByDate(LocalDate date) {
        final String uri = "http://localhost:8080/api?requestDate=" + date;
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }
}

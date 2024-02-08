package com.example.webappaccounting.service;

import com.example.webappaccounting.config.BotConfig;
import com.example.webappaccounting.model.Shift;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {


    final BotConfig config;

    public TelegramBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы с ботом"));
        listOfCommands.add(new BotCommand("/today", "Получить список смены на сегодня"));
        listOfCommands.add(new BotCommand("/tomorrow", "Список смены на завтра"));
        listOfCommands.add(new BotCommand("/2weeks", "Получить список смен на ближайшие две недели"));

        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            log.error("Error bot's command list - " + ex.getMessage());
        }
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

                case "/today": todayCommandReceived(
                        chatId,
                        updateMessage.getChat().getFirstName());
                    break;

                case "/tomorrow": tomorrowCommandReceived(chatId, updateMessage.getFrom().getFirstName());
                    break;

                case "/2weeks": get2WeeksCommandReceived(chatId);
                    break;
                default:
                    sendMessage(chatId, "Не понял вопрос, повторите понятнее. Пока умею только команды из меню, а именно:\n/start\n/today\n/tomorrow\n/2weeks");
            }
            
        } else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String shiftDates = getShiftDates(callbackData);
            String answer = "Ближайшие смены для " + callbackData + ":\n" + shiftDates;
            executeEditMessageText(answer, chatId, messageId);
            //sendMessage(chatId, shiftDates);
            log.info("Пользователь " +
                    update.getCallbackQuery().getFrom().getFirstName() +
                    " запросил ближайшие смены для " + callbackData);

        }

    }

    private String getShiftDates(String callbackData) {
        final String BASE_URL = "http://localhost:8080/api/2week?name=" + callbackData;

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(BASE_URL, String.class);
    }

    private void get2WeeksCommandReceived(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите сотрудника");
        final String BASE_URL = "http://localhost:8080/api/2weeksname";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity =
                restTemplate.getForEntity(BASE_URL, Object[].class);
        Object[] objects = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<String> names = (ArrayList<String>) Arrays.stream(objects)
                .map(object -> mapper.convertValue(object, String.class))
                .sorted()
                .collect(Collectors.toList());

        InlineKeyboardMarkup markupInLine = getInlineKeyboardMarkup(names);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(ArrayList<String> names) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine;
        for (String name : names) {
            rowInLine = new ArrayList<>();
            var button = new InlineKeyboardButton();
            button.setText(name);
            button.setCallbackData(name);
            rowInLine.add(button);
            rowsInLine.add(rowInLine);
        }

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error(ex.getMessage());
        }
    }

    private void tomorrowCommandReceived(long chatId, String username) {
        sendMessage(chatId, findAllByDate(LocalDate.now().plusDays(1)));
        log.info("Пользователь " +
                username +
                " запросил список смены на завтра");
    }

    private void todayCommandReceived(long chatId, String username) {


        sendMessage(chatId, findAllByDate());
        log.info("Пользователь " +
                username +
                " запросил список смены на сегодня");
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
        String answer = "Привет, " + name + ", как дела?\n Введи команду /today для выдачи списка смены на сегодня,\n" +
                "команду /tomorrow для завтрашней смены\n" +
                "или /2weeks для вывода ближайших смен";
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

    private void executeEditMessageText(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}

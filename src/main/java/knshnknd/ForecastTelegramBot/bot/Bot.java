package knshnknd.ForecastTelegramBot.bot;


import knshnknd.ForecastTelegramBot.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private WeatherService weatherService;

    @Scheduled(cron = "0 0 2 * * *")
    public void timeForEverydayForecast() {
        weatherService.sendForecastToAllSubscribed(this);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            String currentChatId = message.getChatId().toString();

            if (message.hasText()) {
                String[] textFromMessage = message.getText().split(" ", 2);

                switch (textFromMessage[0].toLowerCase()) {
                    case "/start@chatovyonokbot", "/start" -> {
                        sendMessage(currentChatId, BotMessages.START_TEXT);
                    }

                    case "/help@chatovyonokbot", "/help" -> {
                        sendMessage(currentChatId, BotMessages.HELP_TEXT);
                    }

                    case "/weather@chatovyonokbot", "/weather", "/we" -> {
                        if (BotUtils.isTextMessageHasAnyWordsMore(textFromMessage)) {
                            sendMessage(currentChatId, weatherService.getFullWeatherForecast(textFromMessage[1].trim()));
                        } else {
                            sendMessage(currentChatId, BotMessages.WEATHER_EMPTY_REQUEST_MESSAGE);
                        }
                    }

                    case "/weather_subscribe@chatovyonokbot", "/weather_subscribe", "/weather_sub" -> {
                        if (BotUtils.isTextMessageHasAnyWordsMore(textFromMessage)) {
                            weatherService.subscribeToWeather(this, currentChatId, textFromMessage[1].trim());
                        } else {
                            sendMessage(currentChatId, BotMessages.WEATHER_SUBSCRIPTION_EMPTY_REQUEST_MESSAGE);
                        }
                    }

                    case "/weather_unsubscribe@chatovyonokbot", "/weather_unsubscribe" -> {
                        weatherService.unsubscribeFromWeather(currentChatId);
                        sendMessage(currentChatId, BotMessages.WEATHER_UNSUBSCRIPTION_MESSAGE);
                    }
                }
            }
        }
    }

    public void sendMessage(String chatId, String message) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .build());
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    @Override
    public String getBotUsername() {
        return BotKeysConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotKeysConfig.BOT_TOKEN;
    }


}

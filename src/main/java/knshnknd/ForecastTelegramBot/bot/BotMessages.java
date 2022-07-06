package knshnknd.ForecastTelegramBot.bot;

/*
help
weather - Check the weather forecast for a city. For example, /weather Paris.
weather_subscribe - Agree that the bot sends a weather forecast once a day to the chat for the city specified after the command. For example: /weather_subscribe Paris.
weather_unsubscribe - Reject the bot to send a weather forecast once a day to the chat.
 */

public class BotMessages {
    public static final String START_TEXT = "Use the /help command to see what I can do!";

    public static final String HELP_TEXT =
            "/weather - Check the weather forecast for a city. For example, /weather Paris.\n" +
            "/weather_subscribe - Agree that the bot sends a weather forecast once a day to the chat for the city specified after the command. For example: /weather_subscribe Paris.\n" +
            "/weather_unsubscribe - Reject the bot to send a weather forecast once a day to the chat.";

    public static final String WEATHER_EMPTY_REQUEST_MESSAGE = "Empty request! Write the name of the city after the command." +
            "\n\nFor example: /weather_subscribe Paris.";

    public static final String WEATHER_SUBSCRIPTION_EMPTY_REQUEST_MESSAGE = "Empty request! Write the name of the city after the command." +
            "So that I send a forecast for it every morning. \n\n" +
            "For example: /weather_subscribe Paris.";

    public static final String WEATHER_UNSUBSCRIPTION_MESSAGE = "Okay! I won't send the weather forecast here every day.";

    public static final String WEATHER_CITY_ERROR = "Misspelled city name for weather forecast.";

}

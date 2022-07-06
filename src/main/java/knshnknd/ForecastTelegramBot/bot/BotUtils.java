package knshnknd.ForecastTelegramBot.bot;

public class BotUtils {

    public static boolean isTextMessageHasAnyWordsMore(String[] textFromMessage) {
        return textFromMessage.length >= 2;
    }

}

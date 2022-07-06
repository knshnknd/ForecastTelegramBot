package knshnknd.ForecastTelegramBot;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import knshnknd.ForecastTelegramBot.bot.Bot;
import knshnknd.ForecastTelegramBot.bot.BotKeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ForecastTelegramBotApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ForecastTelegramBotApplication.class, args);
		Bot bot = context.getBean("bot", Bot.class);

		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(bot);
		} catch (TelegramApiException ex) {
			System.out.println(ex);
		}
	}

	@Bean
	public OpenWeatherMapClient openWeatherClientBean() {
		return new OpenWeatherMapClient(BotKeysConfig.WEATHER_API_KEY);
	}

}

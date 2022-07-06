package knshnknd.ForecastTelegramBot.service;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.exception.NoDataFoundException;
import com.github.prominence.openweathermap.api.model.forecast.*;
import com.github.prominence.openweathermap.api.model.forecast.Location;
import com.github.prominence.openweathermap.api.model.weather.*;
import knshnknd.ForecastTelegramBot.bot.Bot;
import knshnknd.ForecastTelegramBot.bot.BotMessages;
import knshnknd.ForecastTelegramBot.model.entities.WeatherSubscription;
import knshnknd.ForecastTelegramBot.model.repositories.WeatherSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class WeatherService {

    @Autowired
    private OpenWeatherMapClient openWeatherClient;
    @Autowired
    private WeatherSubscriptionRepository weatherSubscriptionRepository;

    private final int HOURS_FOR_FORECAST_X_3 = 3;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public String getFullWeatherForecast(String cityName) {
        return getWeatherForecastForNow(cityName) + "\n\n" + getWeatherForecastFor9Hours(cityName);
    }

    public void addNewUserWeatherIfNotExist(String chatId) {
        Optional<WeatherSubscription> userWeatherOptional = weatherSubscriptionRepository.findUserWeatherByChatId(chatId);
        if (userWeatherOptional.isEmpty()) {
            WeatherSubscription weatherSubscription = new WeatherSubscription(chatId, Boolean.FALSE, "");
            weatherSubscriptionRepository.save(weatherSubscription);
        }
    }

    public void subscribeToWeather(Bot bot, String chatId, String cityName) {
        Optional<WeatherSubscription> userWeatherOptional = weatherSubscriptionRepository.findUserWeatherByChatId(chatId);
        if (userWeatherOptional.isPresent()) {
            WeatherSubscription weatherSubscription = userWeatherOptional.get();
            weatherSubscription.setActive(Boolean.TRUE);
            weatherSubscription.setWeatherCity(cityName.trim());
            weatherSubscriptionRepository.save(weatherSubscription);
            bot.sendMessage(chatId, "Early in the morning I will send a weather forecast for  " +
                    cityName + " to this chat. To cancel this venture, type /weather_unsubscribe.");
        } else {
            addNewUserWeatherIfNotExist(chatId);
            subscribeToWeather(bot, chatId, cityName);
        }
    }

    public void unsubscribeFromWeather(String chatId) {
        Optional<WeatherSubscription> userWeatherOptional = weatherSubscriptionRepository.findUserWeatherByChatId(chatId);
        if (userWeatherOptional.isPresent()) {
            WeatherSubscription weatherSubscription = userWeatherOptional.get();
            weatherSubscription.setActive(Boolean.FALSE);
            weatherSubscriptionRepository.save(weatherSubscription);
        } else {
            addNewUserWeatherIfNotExist(chatId);
            unsubscribeFromWeather(chatId);
        }
    }

    public void sendForecastToAllSubscribed(Bot bot) {
        List<WeatherSubscription> weatherSubscriptionList = weatherSubscriptionRepository.findUserWeatherByIsActive(Boolean.TRUE);
        for (WeatherSubscription weatherSubscription : weatherSubscriptionList) {
            bot.sendMessage(weatherSubscription.getChatId(), getFullWeatherForecast(weatherSubscription.getWeatherCity()));
        }
    }

    private String getWeatherForecastForNow(String cityName) {
        try {
            final Weather weather =
                    openWeatherClient
                            .currentWeather()
                            .single()
                            .byCityName(cityName)
                            .language(Language.ENGLISH)
                            .unitSystem(UnitSystem.IMPERIAL)
                            .retrieve()
                            .asJava();

            // Пишем погоду на данный момент
            return "Weather forecast for "
                    + weather.getLocation().getName()
                    + " at "
                    + timeFormatter.format(getCorrectTemporalAccessorForWeather(weather))
                    + ": air temperature "
                    + weather.getTemperature().getValue()
                    + "°F, humidity "
                    + weather.getHumidity().getValue()
                    + "%, wind speed "
                    + weather.getWind().getSpeed()
                    + " mph, "
                    + weather.getWeatherState().getDescription() + ".";

        } catch (NoDataFoundException e) {
            return BotMessages.WEATHER_CITY_ERROR;
        }
    }

    private String getWeatherForecastFor9Hours(String cityName) {
        StringBuilder stringBuilder = new StringBuilder("Weather forecast for the next " + HOURS_FOR_FORECAST_X_3 * 3 + " hours:\n");

        final Forecast forecast =
                openWeatherClient
                        .forecast5Day3HourStep()
                        .byCityName(cityName)
                        .language(Language.ENGLISH)
                        .unitSystem(UnitSystem.IMPERIAL)
                        .count(HOURS_FOR_FORECAST_X_3)
                        .retrieve()
                        .asJava();

        List<WeatherForecast> weathers = forecast.getWeatherForecasts();

        for (WeatherForecast weatherForecast : weathers) {
            stringBuilder.append("· ")
                    .append(timeFormatter.format(getCorrectTemporalAccessorForForecast(weatherForecast.getForecastTime(), forecast.getLocation())))
                    .append(": ")
                    .append(weatherForecast.getWeatherState().getDescription())
                    .append(", ")
                    .append(weatherForecast.getTemperature().getValue())
                    .append("°F.\n");
        }
        return stringBuilder.toString();
    }

    private TemporalAccessor getCorrectTemporalAccessorForWeather(Weather weather) {
        return weather.getCalculationTime().plusSeconds(weather.getLocation().getZoneOffset().getTotalSeconds());
    }

    private TemporalAccessor getCorrectTemporalAccessorForForecast(LocalDateTime localDateTime, Location location) {
        return localDateTime.plusSeconds(location.getZoneOffset().getTotalSeconds());
    }
}

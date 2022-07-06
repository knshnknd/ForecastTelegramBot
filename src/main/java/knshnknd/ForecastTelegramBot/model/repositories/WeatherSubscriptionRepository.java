package knshnknd.ForecastTelegramBot.model.repositories;

import knshnknd.ForecastTelegramBot.model.entities.WeatherSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherSubscriptionRepository extends JpaRepository<WeatherSubscription, Long> {
    Optional<WeatherSubscription> findUserWeatherByChatId(String chatId);
    List<WeatherSubscription> findUserWeatherByIsActive(Boolean isActive);
}

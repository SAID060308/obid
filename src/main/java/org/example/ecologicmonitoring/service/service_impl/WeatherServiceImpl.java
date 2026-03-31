package org.example.ecologicmonitoring.service.service_impl;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.example.ecologicmonitoring.external.weather.WeatherClient;
import org.example.ecologicmonitoring.repository.WeatherRepository;
import org.example.ecologicmonitoring.service.service_interface.WeatherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherClient weatherClient;
    private final WeatherRepository weatherRepository;

    @Override
    public List<WeatherData> get5DaysForecast(String city, User user) {

        LocalDateTime freshFrom = LocalDateTime.now().minusHours(3);

        List<WeatherData> freshData = weatherRepository
                .findByUserAndCityAndTimestampAfterOrderByTimestampAsc(
                        user, city, freshFrom
                );

        if (freshData.size() >= 5) {
            // ✅ Faqat 5 tasini qaytaramiz
            return freshData.stream().limit(5).toList();
        }

        List<WeatherData> forecast = weatherClient.fetch5DaysForecast(city);

        forecast.forEach(data -> {
            boolean exists = weatherRepository
                    .findByUserAndCityAndTimestamp(user, city, data.getTimestamp())
                    .isPresent();

            if (!exists) {
                data.setUser(user);
                weatherRepository.save(data);
            }
        });

        // ✅ Faqat 5 tasini qaytaramiz
        return forecast.stream().limit(5).toList();
    }
}
package org.example.ecologicmonitoring.external.weather;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeatherClient {

    private final WebClient webClient;
    private final String apiKey = "8441ad16fcfb3ebc94bfbc484692e5a7";

    public List<WeatherData> fetch5DaysForecast(String city) {

        ForecastResponse response = webClient.get()
                .uri("https://api.openweathermap.org/data/2.5/forecast?q={city}&appid={apiKey}&units=metric",
                        city, apiKey)
                .retrieve()
                .bodyToMono(ForecastResponse.class)
                .block();

        List<WeatherData> weatherList = new ArrayList<>();

        if (response != null && response.getList() != null) {

            // ✅ Har kundan faqat 1 ta — 12:00 dagi ma'lumotni olamiz
            Map<String, ForecastResponse.Item> dailyMap = new LinkedHashMap<>();

            for (ForecastResponse.Item item : response.getList()) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(item.getDt()), ZoneId.systemDefault()
                );

                String dateKey = dateTime.toLocalDate().toString(); // "2026-03-30"

                // Har kun uchun soat 12:00 ga yaqin ma'lumotni olamiz
                if (!dailyMap.containsKey(dateKey)) {
                    dailyMap.put(dateKey, item);
                } else {
                    // Agar 12:00 ga yaqinroq bo'lsa — almashtirамiz
                    LocalDateTime existing = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(dailyMap.get(dateKey).getDt()),
                            ZoneId.systemDefault()
                    );
                    int existingDiff = Math.abs(existing.getHour() - 12);
                    int newDiff = Math.abs(dateTime.getHour() - 12);

                    if (newDiff < existingDiff) {
                        dailyMap.put(dateKey, item);
                    }
                }
            }

            // ✅ Faqat 5 kunni ol
            dailyMap.values().stream().limit(5).forEach(item -> {
                WeatherData data = new WeatherData();
                data.setCity(city);
                data.setTemperature(item.getMain().getTemp());
                data.setHumidity(item.getMain().getHumidity());
                data.setPressure(item.getMain().getPressure());
                data.setWindSpeed(item.getWind().getSpeed());
                data.setTimestamp(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(item.getDt()), ZoneId.systemDefault()
                ));
                weatherList.add(data);
            });
        }

        return weatherList;
    }

    // JSON mapping
    @lombok.Data
    public static class ForecastResponse {
        private List<Item> list;

        @lombok.Data
        public static class Item {
            private Long dt;
            private Main main;
            private Wind wind;
        }

        @lombok.Data
        public static class Main {
            private Double temp;
            private Double pressure;
            private Double humidity;
        }

        @lombok.Data
        public static class Wind {
            private Double speed;
        }
    }
}
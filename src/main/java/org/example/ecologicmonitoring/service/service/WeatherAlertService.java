package org.example.ecologicmonitoring.service.service;

import org.example.ecologicmonitoring.entity.WeatherData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherAlertService {

    public List<String> generateAlerts(List<WeatherData> weatherList) {
        List<String> alerts = new ArrayList<>();

        if (weatherList == null || weatherList.isEmpty()) return alerts;

        for (WeatherData w : weatherList) {
            String date = w.getTimestamp().toLocalDate().toString();
            double temp = w.getTemperature();
            double humidity = w.getHumidity();
            double wind = w.getWindSpeed();

            if (temp <= 0) {
                alerts.add("🥶 " + date + " — Harorat " + temp + "°C! Juda sovuq, issiq kiyining va ehtiyot bo'ling.");
            } else if (temp > 0 && temp <= 10) {
                alerts.add("🧥 " + date + " — Harorat " + temp + "°C. Sovuq havo, kalta kiyim kiyib chiqmang.");
            } else if (temp > 10 && temp <= 18) {
                alerts.add("🌤️ " + date + " — Harorat " + temp + "°C. Salqin havo, yengil kurtka tavsiya etiladi.");
            } else if (temp > 18 && temp <= 28) {
                alerts.add("⚽ " + date + " — Harorat " + temp + "°C. Ajoyib kun! Futbol o'ynash yoki sayr qilish uchun ideal.");
            } else if (temp > 28 && temp <= 35) {
                alerts.add("☀️ " + date + " — Harorat " + temp + "°C. Issiq havo, bosh kiyim kiyib chiqing va ko'p suv iching.");
            } else if (temp > 35) {
                alerts.add("🔥 " + date + " — Harorat " + temp + "°C! Juda issiq! Tashqariga chiqishni kamaytiring.");
            }

            if (humidity > 80) {
                alerts.add("🌧️ " + date + " — Namlik " + humidity + "%. Yomg'ir yog'ishi mumkin, soyabon oling!");
            }

            if (wind > 10) {
                alerts.add("💨 " + date + " — Shamol " + wind + " m/s. Kuchli shamol, ehtiyot bo'ling!");
            }
        }

        return alerts;
    }
}
package org.example.ecologicmonitoring.service.service;

import org.example.ecologicmonitoring.entity.WeatherData;
import org.springframework.stereotype.Service;

@Service
public class PlanAdviceService {

    public String generateAdvice(String planText, WeatherData weather) {
        if (weather == null) {
            return "⚠️ Bu kun uchun ob-havo ma'lumoti mavjud emas.";
        }

        double temp = weather.getTemperature();
        double humidity = weather.getHumidity();
        double wind = weather.getWindSpeed();

        String plan = planText.toLowerCase();

        // Tashqi faoliyatlar
        boolean isOutdoor = plan.contains("futbol") || plan.contains("sayr") ||
                plan.contains("sport") || plan.contains("velosiped") ||
                plan.contains("yugurish") || plan.contains("piknik") ||
                plan.contains("tog") || plan.contains("daryo") ||
                plan.contains("beach") || plan.contains("plyaj");

        // Ichki faoliyatlar
        boolean isIndoor = plan.contains("kino") || plan.contains("kitob") ||
                plan.contains("ish") || plan.contains("uchrash") ||
                plan.contains("restoran") || plan.contains("cafe") ||
                plan.contains("xarид") || plan.contains("shopping");

        StringBuilder advice = new StringBuilder();
        boolean suitable = true;

        // Harorat tahlili
        if (temp <= 0) {
            advice.append("🥶 Harorat juda past (").append(temp).append("°C). ");
            if (isOutdoor) {
                advice.append("Tashqi rejangiz uchun mos emas — issiq kiyining yoki ichkaridа qoling. ");
                suitable = false;
            }
        } else if (temp > 0 && temp <= 10) {
            advice.append("🧥 Sovuq havo (").append(temp).append("°C). ");
            if (isOutdoor) {
                advice.append("Tashqarida bo'lsangiz, issiq kiyining. ");
            }
        } else if (temp > 10 && temp <= 25) {
            advice.append("✅ Harorat qulay (").append(temp).append("°C). ");
            if (isOutdoor) {
                advice.append("Tashqi faoliyat uchun ajoyib kun! ");
            }
        } else if (temp > 25 && temp <= 35) {
            advice.append("☀️ Issiq havo (").append(temp).append("°C). ");
            if (isOutdoor) {
                advice.append("Bosh kiyim kiyib, ko'p suv iching. ");
            }
        } else if (temp > 35) {
            advice.append("🔥 Juda issiq (").append(temp).append("°C)! ");
            if (isOutdoor) {
                advice.append("Tashqarida uzoq qolmang. ");
                suitable = false;
            }
        }

        // Namlik tahlili
        if (humidity > 80) {
            advice.append("🌧️ Yomg'ir yog'ishi mumkin (namlik ").append(humidity).append("%). ");
            if (isOutdoor) {
                advice.append("Soyabon oling! ");
                suitable = false;
            }
        }

        // Shamol tahlili
        if (wind > 10) {
            advice.append("💨 Kuchli shamol (").append(wind).append(" m/s). ");
            if (isOutdoor) {
                advice.append("Ehtiyot bo'ling. ");
            }
        }

        // Ichki faoliyat uchun
        if (isIndoor && temp > 10 && temp <= 25 && humidity < 80) {
            advice.append("🌤️ Ob-havo yaxshi, tashqarida ham chiqib olsangiz bo'ladi.");
        }

        // Agar hech narsa topilmasa
        if (advice.length() == 0) {
            advice.append("📋 Rejangiz uchun ob-havo ma'lumotlari tahlil qilindi. ");
            if (temp > 10 && temp <= 25) {
                advice.append("✅ Bugun ajoyib kun!");
            }
        }

        return advice.toString();
    }

    public boolean isSuitable(String planText, WeatherData weather) {
        if (weather == null) return true;

        double temp = weather.getTemperature();
        double humidity = weather.getHumidity();
        double wind = weather.getWindSpeed();

        String plan = planText.toLowerCase();
        boolean isOutdoor = plan.contains("futbol") || plan.contains("sayr") ||
                plan.contains("sport") || plan.contains("velosiped") ||
                plan.contains("yugurish") || plan.contains("piknik") ||
                plan.contains("tog") || plan.contains("daryo");

        if (!isOutdoor) return true;

        return temp > 5 && temp < 35 && humidity < 80 && wind < 12;
    }
}
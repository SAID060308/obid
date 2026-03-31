package org.example.ecologicmonitoring.service.service_interface;

import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.WeatherData;
import java.util.List;

public interface WeatherService {
    List<WeatherData> get5DaysForecast(String city, User user);
}
package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.dto.WeatherDataDTO;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.example.ecologicmonitoring.service.service.WeatherAlertService;
import org.example.ecologicmonitoring.service.service_interface.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MonitoringController {

    private final WeatherService weatherService;
    private final WeatherAlertService weatherAlertService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        List<WeatherData> weatherList = weatherService.get5DaysForecast(user.getCity(), user);
        List<String> alerts = weatherAlertService.generateAlerts(weatherList);

        // ✅ DTO ga convert qiling
        List<WeatherDataDTO> weatherDTOs = weatherList.stream()
                .map(w -> new WeatherDataDTO(
                        w.getCity(),
                        w.getTemperature(),
                        w.getHumidity(),
                        w.getPressure(),
                        w.getWindSpeed(),
                        w.getTimestamp()
                )).toList();

        model.addAttribute("weatherList", weatherDTOs); // DTO yuboriladi
        model.addAttribute("alerts", alerts);
        model.addAttribute("username", user.getFirstName());
        model.addAttribute("currentCity", user.getCity());
        return "dashboard";
    }

    @PostMapping("/search")
    public String searchCity(@RequestParam String city,
                             Model model,
                             Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        List<WeatherData> weatherList = weatherService.get5DaysForecast(city, user);
        List<String> alerts = weatherAlertService.generateAlerts(weatherList);

        // ✅ DTO ga convert qiling
        List<WeatherDataDTO> weatherDTOs = weatherList.stream()
                .map(w -> new WeatherDataDTO(
                        w.getCity(),
                        w.getTemperature(),
                        w.getHumidity(),
                        w.getPressure(),
                        w.getWindSpeed(),
                        w.getTimestamp()
                )).toList();

        model.addAttribute("weatherList", weatherDTOs);
        model.addAttribute("alerts", alerts);
        model.addAttribute("username", user.getFirstName());

        return "dashboard";
    }
}
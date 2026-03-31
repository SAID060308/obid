package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.dto.WeatherDataDTO;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.UserCity;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.example.ecologicmonitoring.repository.UserCityRepository;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.example.ecologicmonitoring.service.service.WeatherAlertService;
import org.example.ecologicmonitoring.service.service_interface.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final UserCityRepository userCityRepository;
    private final UserRepository userRepository;
    private final WeatherService weatherService;
    private final WeatherAlertService weatherAlertService;

    // Shaharlar ro'yxati
    @GetMapping
    public String cities(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserCity> cities = userCityRepository.findByUserOrderByAddedAtDesc(user);

        model.addAttribute("cities", cities);
        model.addAttribute("username", user.getFirstName());
        return "cities";
    }

    // Yangi shahar qo'shish
    @PostMapping("/add")
    public String addCity(@RequestParam String cityName,
                          Principal principal,
                          Model model) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Duplicate tekshirish
        if (userCityRepository.existsByUserAndCityName(user, cityName)) {
            return "redirect:/cities?exists";
        }

        UserCity userCity = UserCity.builder()
                .cityName(cityName)
                .user(user)
                .build();

        userCityRepository.save(userCity);
        return "redirect:/cities?added";
    }

    @Transactional
    @PostMapping("/delete/{id}")
    public String deleteCity(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        userCityRepository.deleteByIdAndUser(id, user);
        return "redirect:/cities?deleted";
    }

    @GetMapping("/weather/{cityName}")
    public String cityWeather(@PathVariable String cityName,
                              Model model,
                              Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // ✅ Userni shahri yangilanadi
        user.setCity(cityName);
        userRepository.save(user);
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

}
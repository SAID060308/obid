package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.dto.WeatherDataDTO;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.example.ecologicmonitoring.repository.UserCityRepository;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.example.ecologicmonitoring.repository.WeatherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final UserCityRepository userCityRepository;

    @GetMapping()
    public String history(
            @RequestParam(required = false) String city,
            Model model,
            Principal principal) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        List<WeatherData> historyList;

        // Shahar bo'yicha filter
        if (city != null && !city.isEmpty()) {
            historyList = weatherRepository
                    .findByUserAndCityAndTimestampBeforeOrderByTimestampDesc(
                            user, city, LocalDateTime.now()
                    );
        } else {
            historyList = weatherRepository
                    .findByUserAndTimestampBeforeOrderByTimestampDesc(
                            user, LocalDateTime.now()
                    );
        }

        // DTO ga convert
        List<WeatherDataDTO> historyDTOs = historyList.stream()
                .map(w -> new WeatherDataDTO(
                        w.getCity(), w.getTemperature(), w.getHumidity(),
                        w.getPressure(), w.getWindSpeed(), w.getTimestamp()
                )).toList();

        // Shaharlar ro'yxati filter uchun
        var cities = userCityRepository.findByUserOrderByAddedAtDesc(user);

        model.addAttribute("historyList", historyDTOs);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedCity", city);
        model.addAttribute("username", user.getFirstName());
        return "history";
    }
}
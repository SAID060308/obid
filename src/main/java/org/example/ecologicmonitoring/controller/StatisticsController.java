package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.UserPlan;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.example.ecologicmonitoring.repository.UserPlanRepository;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.example.ecologicmonitoring.repository.WeatherRepository;
import org.example.ecologicmonitoring.service.service.PlanAdviceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserRepository userRepository;
    private final UserPlanRepository userPlanRepository;
    private final WeatherRepository weatherRepository;
    private final PlanAdviceService planAdviceService;

    @GetMapping
    public String statistics(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserPlan> plans = userPlanRepository.findByUserOrderByPlanDateAsc(user);

        model.addAttribute("plans", plans);
        model.addAttribute("username", user.getFirstName());
        model.addAttribute("today", LocalDate.now());
        return "statistics";
    }

    @PostMapping("/add")
    public String addPlan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam String planText,
            Principal principal) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // O'sha kunning ob-havosini ol
        List<WeatherData> weatherList = weatherRepository
                .findByUserAndCityAndTimestampAfterOrderByTimestampAsc(
                        user, user.getCity(),
                        planDate.atStartOfDay()
                );

        WeatherData weather = weatherList.isEmpty() ? null : weatherList.get(0);

        String advice = planAdviceService.generateAdvice(planText, weather);
        boolean suitable = planAdviceService.isSuitable(planText, weather);

        // Agar bu kun uchun reja bo'lsa — yangilash
        UserPlan plan = userPlanRepository
                .findByUserAndPlanDate(user, planDate)
                .orElse(new UserPlan());

        plan.setUser(user);
        plan.setPlanDate(planDate);
        plan.setPlanText(planText);
        plan.setWeatherAdvice(advice);
        plan.setSuitable(suitable);

        userPlanRepository.save(plan);
        return "redirect:/statistics";
    }

    @PostMapping("/delete/{id}")
    public String deletePlan(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        userPlanRepository.findById(id).ifPresent(plan -> {
            if (plan.getUser().getId().equals(user.getId())) {
                userPlanRepository.delete(plan);
            }
        });
        return "redirect:/statistics";
    }
}
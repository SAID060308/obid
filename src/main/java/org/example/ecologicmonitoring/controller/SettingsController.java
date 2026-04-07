package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String settings(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("username", user.getFirstName());
        return "settings";
    }

    // Profil tahrirlash
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String city,
            @RequestParam String email,
            Principal principal,
            Model model) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Email o'zgargan bo'lsa tekshirish
        if (!user.getEmail().equals(email)) {
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("user", user);
                model.addAttribute("username", user.getFirstName());
                model.addAttribute("profileError", "Bu email allaqachon band!");
                return "settings";
            }
            user.setEmail(email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCity(city);
        userRepository.save(user);

        return "redirect:/settings?profileUpdated";
    }

    // Parol o'zgartirish
    @PostMapping("/password")
    public String updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Joriy parolni tekshirish
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getFirstName());
            model.addAttribute("passwordError", "Joriy parol noto'g'ri!");
            return "settings";
        }

        // Yangi parollar mos kelishini tekshirish
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getFirstName());
            model.addAttribute("passwordError", "Yangi parollar mos kelmadi!");
            return "settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "redirect:/settings?passwordUpdated";
    }

    @PostMapping("/delete")
    public String deleteAccount(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        userRepository.delete(user);
        return "redirect:/login?deleted";
    }

}
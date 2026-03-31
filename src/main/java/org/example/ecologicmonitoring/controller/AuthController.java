package org.example.ecologicmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.example.ecologicmonitoring.service.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute User user,
            @RequestParam String confirmPassword,
            Model model) {

        // Parol tekshirish
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Parollar mos kelmadi!");
            model.addAttribute("user", user);
            return "register";
        }

        // Username tekshirish
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Bu username allaqachon band!");
            model.addAttribute("user", user);
            return "register";
        }

        // Email tekshirish
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Bu email allaqachon ro'yxatdan o'tgan!");
            model.addAttribute("user", user);
            return "register";
        }

        // Verification code yaratish
        String code = String.format("%06d", new Random().nextInt(999999));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(code);
        user.setEmailVerified(false);
        userRepository.save(user);

        // Email yuborish
        emailService.sendVerificationCode(user.getEmail(), code);

        return "redirect:/verify?email=" + user.getEmail();
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyCode(
            @RequestParam String email,
            @RequestParam String code,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Foydalanuvchi topilmadi!");
            model.addAttribute("email", email);
            return "verify";
        }

        if (!user.getVerificationCode().equals(code)) {
            model.addAttribute("error", "Kod noto'g'ri! Qayta urinib ko'ring.");
            model.addAttribute("email", email);
            return "verify";
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        return "redirect:/login?registered";
    }

    // Kodni qayta yuborish
    @PostMapping("/resend-code")
    public String resendCode(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && !user.isEmailVerified()) {
            String newCode = String.format("%06d", new Random().nextInt(999999));
            user.setVerificationCode(newCode);
            userRepository.save(user);
            emailService.sendVerificationCode(email, newCode);
        }

        model.addAttribute("email", email);
        model.addAttribute("success", "Kod qayta yuborildi!");
        return "verify";
    }
}
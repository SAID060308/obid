package org.example.ecologicmonitoring.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Eco Monitoring — Email tasdiqlash kodi");
        message.setText(
                "Assalomu alaykum!\n\n" +
                        "Email manzilingizni tasdiqlash uchun quyidagi kodni kiriting:\n\n" +
                        "🔐 Tasdiqlash kodi: " + code + "\n\n" +
                        "Kod 10 daqiqa davomida amal qiladi.\n\n" +
                        "Eco Monitoring tizimi"
        );
        mailSender.send(message);
    }
}
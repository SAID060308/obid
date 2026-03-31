package org.example.ecologicmonitoring.service.service;

import lombok.RequiredArgsConstructor;
import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi: " + username));

        // ✅ ANA SHU YERGA — email verified tekshiruvi
        if (!user.isEmailVerified()) {
            throw new DisabledException("Email tasdiqlanmagan!");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
package com.fanele.capitalflow.config;

import com.fanele.capitalflow.entity.UserEntity;
import com.fanele.capitalflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(2)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmailIgnoreCase("thabo@fanele.com").ifPresentOrElse(user -> {
            if (user.getPhone() == null || user.getPhone().isBlank()) {
                user.setPhone("+27 (11) 555-0142");
            }
            if (user.getBio() == null || user.getBio().isBlank()) {
                user.setBio("Seasoned institutional investor with over two decades of experience in private equity and venture capital across Sub-Saharan Africa. Focused on long-term value creation and transparent portfolio governance.");
            }
            userRepository.save(user);
        }, () -> {
            UserEntity user = new UserEntity(
                    "thabo@fanele.com",
                    passwordEncoder.encode("password123"),
                    "Thabo",
                    "Nkosi",
                    "Fanele & Partners",
                    LocalDate.of(1957, 3, 15)
            );
            user.setPhone("+27 (11) 555-0142");
            user.setBio("Seasoned institutional investor with over two decades of experience in private equity and venture capital across Sub-Saharan Africa. Focused on long-term value creation and transparent portfolio governance.");
            userRepository.save(user);
        });
    }
}

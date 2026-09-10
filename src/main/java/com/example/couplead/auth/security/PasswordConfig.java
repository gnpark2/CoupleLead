package com.example.couplead.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.couplead.auth.performance.TimedPasswordEncoder;

@Configuration
public class PasswordConfig {
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    @Bean
PasswordEncoder passwordEncoder() {
    PasswordEncoder delegate =
        new BCryptPasswordEncoder();

    return new TimedPasswordEncoder(delegate);
}
}

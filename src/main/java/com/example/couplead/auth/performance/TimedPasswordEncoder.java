package com.example.couplead.auth.performance;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimedPasswordEncoder implements PasswordEncoder {

    private final PasswordEncoder delegate;

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(
            CharSequence rawPassword,
            String encodedPassword) {
        long start = System.nanoTime();

        try {
            return delegate.matches(
                    rawPassword,
                    encodedPassword);
        } finally {
            LoginPerformanceContext.recordPasswordCheck(
                    System.nanoTime() - start);
        }
    }

    @Override
    public boolean upgradeEncoding(
            String encodedPassword) {
        return delegate.upgradeEncoding(encodedPassword);
    }
}
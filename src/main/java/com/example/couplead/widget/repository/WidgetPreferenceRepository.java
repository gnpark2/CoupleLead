package com.example.couplead.widget.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.user.domain.User;
import com.example.couplead.widget.domain.WidgetPreference;

public interface WidgetPreferenceRepository extends JpaRepository<WidgetPreference, Long> {
    Optional<WidgetPreference> findByUser(User user);
}

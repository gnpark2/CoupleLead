package com.example.couplead.widget.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.user.domain.User;
import com.example.couplead.widget.domain.WidgetPreference;

public interface WidgetPreferenceRepository extends JpaRepository<WidgetPreference, Long> {
    Optional<WidgetPreference> findByUser(User user);

    @Modifying
    @Query("""
                update WidgetPreference w
                   set w.selectedAnniversary = null
                 where w.selectedAnniversary = :anniversary
            """)
    void clearSelectedAnniversary(
            @Param("anniversary") Anniversary anniversary);
}

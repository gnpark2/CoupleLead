package com.example.couplead.widget.domain;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.common.entity.BaseEntity;
import com.example.couplead.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "widget_preferences", uniqueConstraints = {
        @UniqueConstraint(name = "uk_widget_preference_user", columnNames = "user_id")
})
public class WidgetPreference extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_anniversary_id")
    private Anniversary selectedAnniversary;

    public void selectAnniversary(Anniversary anniversary) {
        this.selectedAnniversary = anniversary;
    }

    public void clearSelectedAnniversary() {
        this.selectedAnniversary = null;
    }
}

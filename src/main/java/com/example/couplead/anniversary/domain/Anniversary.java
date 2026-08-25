package com.example.couplead.anniversary.domain;

import java.time.LocalDate;

import com.example.couplead.common.entity.BaseEntity;
import com.example.couplead.couple.domain.Couple;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "anniversaries")
public class Anniversary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false)
    private Couple couple;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private LocalDate anniversaryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnniversaryType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepeatType repeatType;

    @Column(name = "custom_type_name", length = 30)
    private String customTypeName;

    public void update(
            String title,
            LocalDate anniversaryDate,
            AnniversaryType type,
            RepeatType repeatType,
            String customTypeName) {
        this.title = title;
        this.anniversaryDate = anniversaryDate;
        this.type = type;
        this.repeatType = repeatType;
        this.customTypeName = customTypeName;
    }
}

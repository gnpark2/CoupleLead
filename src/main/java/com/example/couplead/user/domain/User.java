package com.example.couplead.user.domain;

import com.example.couplead.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_nickname", columnNames = "nickname")
})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String country;

    private String city;

    private String timezone;

    private Double latitude;

    private Double longitude;

    public void updateLocation(
            String country,
            String city,
            String timezone,
            Double latitude,
            Double longitude) {
        this.country = country;
        this.city = city;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateNickname(
            String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(
            String profileImage) {
        this.profileImage = profileImage;
    }

    public void removeProfileImage() {
        this.profileImage = null;
    }

    public void changePassword(
            String password) {
        this.password = password;
    }
}

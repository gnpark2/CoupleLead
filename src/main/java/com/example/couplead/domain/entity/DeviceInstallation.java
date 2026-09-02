package com.example.couplead.domain.entity;

import com.example.couplead.user.domain.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_installations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_device_fid", columnNames = "fid")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeviceInstallation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fid", nullable = false, length = 255)
    private String fid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DevicePlatform platform;

    @Column(name = "fcm_token", nullable = false, length = 512)
    private String fcmToken;

    public void updateOwner(
            User user,
            String fcmToken,
            DevicePlatform platform) {
        this.user = user;
        this.fcmToken = fcmToken;
        this.platform = platform;
    }
}
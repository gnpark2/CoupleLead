package com.example.couplead.anniversary.domain;

import com.example.couplead.user.domain.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_anniversary_selections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "user_id",
                "anniversary_id"
        })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HomeAnniversarySelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anniversary_id", nullable = false)
    private Anniversary anniversary;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
package com.tradeflux.user_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_profile_email",
                        columnNames = "email"
                ),

                @UniqueConstraint(
                        name = "uk_user_profile_username",
                        columnNames = "username"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long authUserId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;
    private String lastName;


    @Column(length = 1000)
    private String bio;

    private String tradingExperience;
    private String riskProfile;


    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

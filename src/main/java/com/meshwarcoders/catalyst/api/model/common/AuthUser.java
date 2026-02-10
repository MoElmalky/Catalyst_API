package com.meshwarcoders.catalyst.api.model.common;

import com.meshwarcoders.catalyst.api.model.RefreshTokenModel;
import com.meshwarcoders.catalyst.api.model.UserDeviceModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter @Setter
public abstract class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String username;

    private Boolean emailConfirmed = false;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private String resetPasswordToken;

    private String emailConfirmToken;

    @OneToMany
    private List<RefreshTokenModel> refreshTokens = new ArrayList<>();

    @OneToMany
    private List<UserDeviceModel> userDevices = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

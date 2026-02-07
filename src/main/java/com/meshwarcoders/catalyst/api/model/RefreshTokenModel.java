package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.AuthUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "refresh_tokens")
@Getter @Setter
public class RefreshTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private TeacherModel teacher;

    @ManyToOne
    private StudentModel student;

    private String familyId;

    private String token;

    private Instant expiresAt;

    private Boolean revoked;

    public AuthUser getUser() {
        return teacher != null ? (AuthUser) teacher : student;
    }
}

package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "user_device")
@Getter @Setter
public class UserDeviceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private TeacherModel teacher;

    @ManyToOne
    private StudentModel student;

    private String deviceType;

    private String deviceId;

    private String token;

    private Instant lastActive;
}

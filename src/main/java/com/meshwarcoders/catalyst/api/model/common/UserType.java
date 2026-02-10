package com.meshwarcoders.catalyst.api.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserType {
    STUDENT,
    TEACHER;

    @JsonCreator
    public static UserType fromString(String value) {
        return switch (value.toLowerCase().trim()) {
            case "teacher", "t" -> TEACHER;
            case "student", "s" -> STUDENT;
            default -> throw new IllegalArgumentException("Invalid role");
        };
    }
}

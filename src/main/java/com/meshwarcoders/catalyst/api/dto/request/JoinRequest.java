package com.meshwarcoders.catalyst.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record JoinRequest(@NotEmpty(message = "studentIds cannot be empty") List<Long> studentLessonIds) {
}
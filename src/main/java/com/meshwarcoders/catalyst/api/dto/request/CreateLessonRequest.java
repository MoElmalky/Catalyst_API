package com.meshwarcoders.catalyst.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateLessonRequest(@NotBlank(message = "subject is required") String subject,
                                  @Valid @NotEmpty(message = "lessonSchedules is required") List<LessonScheduleRequest> lessonSchedules) {

}

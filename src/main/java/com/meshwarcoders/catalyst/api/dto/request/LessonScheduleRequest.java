package com.meshwarcoders.catalyst.api.dto.request;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.validation.constraints.NotNull;

public record LessonScheduleRequest(@NotNull(message = "duration is required") Integer duration,
                                    @NotNull(message = "startTime is required") String startTime,
                                    @NotNull(message = "day is required") WeekDay day) {
}

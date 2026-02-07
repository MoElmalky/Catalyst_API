package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.dto.response.LessonScheduleDto;

import java.util.List;

public record LessonSummaryDto(Long id,
                               String subject,
                               List<LessonScheduleDto> lessonSchedules) {
}

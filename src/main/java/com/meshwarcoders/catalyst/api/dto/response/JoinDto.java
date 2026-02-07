package com.meshwarcoders.catalyst.api.dto.response;

import java.util.List;

public record JoinDto(Long lessonId,
                      List<Long> affectedStudentLessonsIds,
                      List<Long> skippedStudentLessonsIds) {
}
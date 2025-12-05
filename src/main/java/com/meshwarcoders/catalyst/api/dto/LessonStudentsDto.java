package com.meshwarcoders.catalyst.api.dto;

import java.util.List;

public record LessonStudentsDto(Integer studentsNumber,
                                List<StudentDetailsDto> students) {
}

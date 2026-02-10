package com.meshwarcoders.catalyst.api.dto.response;

import java.util.List;

public record LessonStudentsDto(Integer studentsNumber,
                                List<StudentDetailsDto> students) {
}

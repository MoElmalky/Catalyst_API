package com.meshwarcoders.catalyst.api.dto.response;

import java.util.List;

public record StudentExamAnswersResponseDto(
        String studentName,
        Integer totalGrade,
        List<StudentAnswerResponseDto> answers) {
}

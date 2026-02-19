package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.common.ExamType;
import java.util.List;

public record ExamDetailsDto(
                Long id,
                Long lessonId,
                String examName,
                Integer maxGrade,
                String examDateTime,
                String closingDate,
                Integer durationMinutes,
                ExamType examType,
                List<QuestionDto> questions,
                List<StudentExamDto> studentGrads
                ) {
}

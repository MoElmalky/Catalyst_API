package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.ExamModel;
import com.meshwarcoders.catalyst.api.model.common.ExamType;

public record ExamSummaryDto(
                Long id,
                Long lessonId,
                String examName,
                Integer maxGrade,
                String examDateTime,
                String closingDate,
                Integer durationMinutes,
                ExamType examType) {

}
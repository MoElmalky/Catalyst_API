package com.meshwarcoders.catalyst.api.dto;

import java.util.List;

public record LessonExamsDto(Integer examsNumber, List<ExamSummaryDto> exams) { }

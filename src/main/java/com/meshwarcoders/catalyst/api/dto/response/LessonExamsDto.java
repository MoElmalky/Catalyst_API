package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.dto.response.ExamSummaryDto;

import java.util.List;

public record LessonExamsDto(Integer examsNumber, List<ExamSummaryDto> exams) { }

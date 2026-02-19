package com.meshwarcoders.catalyst.api.dto.response;

import java.util.List;

public record StudentAnswerResponseDto(
        QuestionDto question,
        List<Integer> selectedOptions,
        String textAnswer,
        Double mark) {
}

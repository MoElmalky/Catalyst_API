package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.common.QuestionType;
import java.util.List;

public record QuestionDto(
        Long id,
        String text,
        QuestionType type,
        List<String> options,
        Integer maxPoints) {
}
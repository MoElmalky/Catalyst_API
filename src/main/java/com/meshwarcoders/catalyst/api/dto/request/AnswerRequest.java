package com.meshwarcoders.catalyst.api.dto.request;

import java.util.List;

public record AnswerRequest(
        Long questionId,
        List<Integer> selectedOptions,
        String textAnswer) {
}

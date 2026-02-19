package com.meshwarcoders.catalyst.api.dto.response;

public record StudentExamDto(Long id, String studentName, Integer grade,
                             Boolean verified) {
}

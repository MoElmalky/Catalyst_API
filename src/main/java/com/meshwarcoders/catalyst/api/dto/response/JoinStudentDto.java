package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;

public record JoinStudentDto(Long id,
                             Long lessonId,
                             StudentSummaryDto student,
                             EnrollmentStatus status) {
}
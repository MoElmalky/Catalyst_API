package com.meshwarcoders.catalyst.api.dto.response;

import java.time.Instant;

public record TeacherResponse(Long id,
                              String fullName,
                              String email,
                              boolean emailConfirmed,
                              Instant createdAt) {

}
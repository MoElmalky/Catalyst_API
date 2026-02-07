package com.meshwarcoders.catalyst.api.dto.response;

public record AuthResponse(String accessToken,
                           String refreshToken,
                           Long id,
                           String email,
                           boolean isConfirmed) {
}

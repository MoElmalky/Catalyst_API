package com.meshwarcoders.catalyst.api.dto.response;

public record RefreshDto(
        String accessToken,
        String refreshToken
) {
}

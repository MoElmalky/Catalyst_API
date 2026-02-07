package com.meshwarcoders.catalyst.api.dto.request;

public record DeviceDataRequest(String fcmToken, String deviceType, String deviceId) {
}

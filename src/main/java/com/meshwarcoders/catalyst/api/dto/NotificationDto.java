package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

public class NotificationDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private Map<String, Object> payload;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationDto(Long id, NotificationType type, String title, String body,
                           Map<String, Object> payload, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.body = body;
        this.payload = payload;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}